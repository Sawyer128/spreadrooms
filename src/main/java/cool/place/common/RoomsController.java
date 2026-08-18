package cool.place.common;

import cool.place.Spreadrooms;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomsController {

    private static final int timeToSpread = RoomsConfig.get().timeToSpread;
    private static final int timeToSpreadVariation = RoomsConfig.get().timeToSpreadVariation;
    private static final boolean timedSpread = RoomsConfig.get().timedSpread;

    private static int spreadTimer = 0;

    public static final List<Direction> directions = new ArrayList<>();
    public static final List<Room> rooms = new ArrayList<>();
    public static final List<Room> newRooms = new ArrayList<>();
    public static final List<Room> completeRooms = new ArrayList<>();


    public static boolean begun = false;
    public static BlockPos center;
    public static BlockPos origin;
    public static Room.Size defaultSize = new Room.Size(6, 4, 5);

    static{
        directions.add(Direction.NORTH);
        directions.add(Direction.EAST);
        directions.add(Direction.SOUTH);
        directions.add(Direction.WEST);
        directions.add(Direction.DOWN);
        directions.add(Direction.UP);
    }

    public static void start(MinecraftServer server, Player player) {

        begun = true;
        center = player.blockPosition();

        Level overworld = server.getLevel(ServerLevel.OVERWORLD);

        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "worldborder center " + center.getX() + " " + center.getZ());
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "worldborder set " + RoomsConfig.get().areaFromCenter*2);

        origin = new BlockPos(center.getX() + RoomsConfig.get().areaFromCenter, -64, center.getZ() + RoomsConfig.get().areaFromCenter);
        List<RoomFeature> originFeatures = new ArrayList<>();
        originFeatures.add(RoomFeature.LIGHT);
        rooms.add(new Room(origin, new Room.Size(6, 6, 6), originFeatures));

        if (overworld != null) {
            Spreadrooms.LOGGER.info("Level found, creating origin.");
            overworld.setBlock(origin, RoomFeature.FLOOR.getState(), 3);
        }

    }

    public static void stop(MinecraftServer server, Player player) {
        begun = false;
        center = null;
        origin = null;
        spreadTimer = 0;
    }

    public static void tickSpread(MinecraftServer server) {

        Level overworld = server.getLevel(ServerLevel.OVERWORLD);

        if (begun && origin != null && center != null && overworld != null) {

            if (!timedSpread) {

                loopRooms(overworld, true);

            } else {

                spreadTimer++;

                Random random = new Random();

                if (spreadTimer >= timeToSpread+(random.nextInt(timeToSpreadVariation*2)-timeToSpreadVariation)) {

                    spreadTimer = 0;

                    loopRooms(overworld, true);

                }

            }

        }

    }

    private static void loopRooms(Level level, boolean createRooms) {

        for (Room room : rooms) {
            if (!completeRooms.contains(room)) {

                if (room.isComplete(level)) Spreadrooms.LOGGER.info("Room complete!");

                if (!room.isComplete(level)) room.spread(level, createRooms);
                else completeRooms.add(room);
            }
        }

        if (rooms.size() < RoomsConfig.get().maxGenRooms && !newRooms.isEmpty()) {
            rooms.add(newRooms.getFirst());
            newRooms.removeFirst();
            //rooms.addAll(newRooms.subList(0, RoomsConfig.get().maxRooms-rooms.size()));
        }

        rooms.removeAll(completeRooms);

    }

    /*
    public static void spreadViaRoom(Level level, BlockPos pos) {

        BlockStatePosPair spread = spread(level, pos, new ArrayList<>(), new ArrayList<>());

        if (spread != null) {

            BlockState spreadState = spread.state;
            BlockPos spreadPos = spread.pos;

            BlockState spreadState = null;

            for (Room room : rooms) {
                if (room.contains(spreadPos) && !room.isComplete(level)) {
                    spreadState = room.getStateAt(spreadPos);
                }
            }

            if (spreadState != null) level.setBlock(spreadPos, spreadState, 3);

            level.setBlock(spreadPos, spreadState, 3);

        }

    }

    public static void spread(Level level, BlockPos pos) {

        BlockPosPair searchPair = search(level, pos);

        if (searchPair != null) {

            BlockPos currentPos = searchPair.current;
            BlockPos spreadPos = searchPair.spread;
            BlockState currentState = level.getBlockState(currentPos);
            BlockState spreadState = currentState;

            if (spreadPos.getY() != currentPos.getY()) {

                if (currentState.is(Blocks.WOOL.brown())) {

                    if (spreadPos.getY() > currentPos.getY()) {
                        spreadState = Blocks.STRIPPED_BIRCH_LOG.defaultBlockState();
                    } else if (spreadPos.getY() < currentPos.getY()) {
                        spreadState = Blocks.IRON_BLOCK.defaultBlockState();
                    }

                }

                if (currentState.is(Blocks.STRIPPED_BIRCH_LOG)) {

                    if (spreadPos.getY() > currentPos.getY()) {
                        spreadState = Blocks.IRON_BLOCK.defaultBlockState();
                    } else if (spreadPos.getY() < currentPos.getY()) {
                        spreadState = Blocks.WOOL.brown().defaultBlockState();
                    }


                }

                if (currentState.is(Blocks.IRON_BLOCK)) {

                    if (spreadPos.getY() > currentPos.getY()) {
                        spreadState = Blocks.WOOL.brown().defaultBlockState();
                    } else if (spreadPos.getY() < currentPos.getY()) {
                        spreadState = Blocks.STRIPPED_BIRCH_LOG.defaultBlockState();
                    }
                }
            }

            level.setBlock(spreadPos, spreadState, 3);

        }

    }

    public static BlockPosPair search(Level level, BlockPos pos) {

        Random random = new Random();
        List<Direction> tempDirections = new ArrayList<Direction>(directions);

        int i = directions.indexOf(directions.get(random.nextInt(directions.size())));
        BlockPos spreadToPos = pos.relative(directions.get(i));
        while (!isWithinBounds(spreadToPos)) {
            tempDirections.remove(i);
            if (!tempDirections.isEmpty()) {
                i = tempDirections.indexOf(tempDirections.get(random.nextInt(tempDirections.size())));
                spreadToPos = pos.relative(tempDirections.get(i));
            }
        }

        if (isWithinBounds(spreadToPos) && !isInfected(level, spreadToPos)) { return new BlockPosPair(pos, spreadToPos); }
        else if (isWithinBounds(spreadToPos) && isInfected(level, spreadToPos)) { return search(level, spreadToPos); }

        return null;

    }

    public static BlockPos searchViaRoom(Level level, BlockPos pos, List<Room> completeRooms, List<BlockPos> completeBlocks) {

        Random random = new Random();
        List<Direction> tempDirections = new ArrayList<Direction>(directions);
        BlockPos spreadToPos = null;
        int i;

        while (!tempDirections.isEmpty() && completeBlocks.contains(spreadToPos = pos.relative(tempDirections.get(i = tempDirections.indexOf(tempDirections.get(random.nextInt(tempDirections.size())))))) && !isWithinBounds(spreadToPos)) {
            tempDirections.remove(i);
        }

        boolean inCompleteRoom = false;
        if (spreadToPos != null) for (Room completeRoom : completeRooms) { if (completeRoom.contains(spreadToPos)) inCompleteRoom = true; }

        if (spreadToPos != null && isWithinBounds(spreadToPos) && !isInfected(level, spreadToPos) && !inCompleteRoom) {

            for (Room room : rooms) {
                if (room.contains(spreadToPos) && !room.isComplete(level)) return spreadToPos;
                else if (room.contains(spreadToPos) && room.isComplete(level)) { completeRooms.add(room); searchViaRoom(level, pos, completeRooms, completeBlocks); }
            }

            rooms.add(new Room(pos, Room.resizeToBounds(pos, new Room.Size(5, 5, 5), rooms), RoomFeature.NONE));
            return pos;

        }
        else if (spreadToPos != null && isWithinBounds(spreadToPos) && isInfected(level, spreadToPos)) { completeBlocks.add(pos); return searchViaRoom(level, spreadToPos, completeRooms, completeBlocks); }

        return null;

    }

    public static BlockStatePosPair spread(Level level, BlockPos pos, List<Room> completeRooms, List<BlockPos> completeBlocks) {

        List<Direction> tempDirections = new ArrayList<Direction>(directions);
        Collections.shuffle(tempDirections);
        BlockPos potentialPos;

        for (Direction dir : tempDirections) {

            potentialPos = pos.relative(dir);

            if (!completeBlocks.contains(potentialPos) && isWithinBounds(potentialPos)) {

                for (Room room : rooms) {

                    if (completeRooms.contains(room) && room.contains(potentialPos)) {

                        completeBlocks.add(pos);
                        return spread(level, potentialPos, completeRooms, completeBlocks);

                    }

                    if (room.contains(potentialPos) && !room.isComplete(level)) {

                        if (level.getBlockState(potentialPos) != room.getStateAt(potentialPos)) {
                            return new BlockStatePosPair(room.getStateAt(potentialPos), potentialPos);
                        } else {
                            completeBlocks.add(pos);
                            return spread(level, potentialPos, completeRooms, completeBlocks);
                        }

                    } else if (room.contains(potentialPos) && room.isComplete(level)) {

                        completeRooms.add(room);
                        completeBlocks.add(pos);
                        return spread(level, potentialPos, completeRooms, completeBlocks);

                    }

                }

                Room newRoom = new Room(pos, Room.resizeToBounds(pos, new Room.Size(5, 5, 5), rooms), RoomFeature.NONE);
                rooms.add(newRoom);
                return new BlockStatePosPair(newRoom.getStateAt(potentialPos), potentialPos);

            }
        }

        return null;
    }

    public static boolean isWithinBounds(BlockPos pos) {

        if (pos.getX() < (center.getX() - RoomsConfig.get().areaFromCenter - 1)
                || pos.getX() > (center.getX() + RoomsConfig.get().areaFromCenter + 1)
                || pos.getZ() < (center.getZ() - RoomsConfig.get().areaFromCenter - 1)
                || pos.getZ() > (center.getZ() + RoomsConfig.get().areaFromCenter + 1)
                || pos.getY() < -64) return false;

        return true;
    }

    public static boolean isInfected(Level level, BlockPos pos) {

        if (level.getBlockState(pos).is(Blocks.WOOL.brown())
                || level.getBlockState(pos).is(Blocks.STRIPPED_BIRCH_LOG)
                || level.getBlockState(pos).is(Blocks.IRON_BLOCK)) return true;

        return false;
    }
    */

}
