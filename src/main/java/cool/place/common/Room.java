package cool.place.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public record Room(BlockPos origin, Size size, List<RoomFeature> features) {

    public record Size (int x, int y, int z) { }

    private record BlockPosStateBoolTriple(BlockPos pos, BlockState state, boolean outside) {}

    public void spread(Level level, boolean createRooms) {

        BlockPosStateBoolTriple result = null;
        if (level.getBlockState(origin).is(getStateAt(origin).getBlock())) result = search(level, origin, new ArrayList<>(), createRooms);
        else result = new BlockPosStateBoolTriple(origin, getStateAt(origin), false);

        if (result != null) {

            if (!result.outside) {
                level.setBlock(result.pos, result.state, 3);
            } else {
                Room newRoom = createRoomProperties(result.pos);
                if (newRoom != null) createRoom(newRoom, level);
            }
        }
    }

    private Room createRoomProperties(BlockPos pos) {

        List<RoomFeature> newFeatures = new ArrayList<>();
        Random random = new Random();

        int lightSpecial = random.nextInt(20);
        if (lightSpecial <= 10) newFeatures.add(RoomFeature.LIGHT);
        else if (lightSpecial <= 15) newFeatures.add(RoomFeature.MULTILIGHT);

        if (random.nextBoolean()) newFeatures.add(RoomFeature.BASEBOARDS);

        int wallsSpecial = random.nextInt(20);
        if (wallsSpecial <= 5) newFeatures.add(RoomFeature.BAMBOO);
        else if (wallsSpecial <= 10) newFeatures.add(RoomFeature.SMOOTH);
        if (wallsSpecial == 11) newFeatures.add(RoomFeature.MOSAIC);
        if (wallsSpecial == 12) newFeatures.add(RoomFeature.GREEN);

        int floorsSpecial = random.nextInt(10);
        if (floorsSpecial <= 2) newFeatures.add(RoomFeature.CARPET);

        /*int faceMissing = random.nextInt(40);
        if (faceMissing == 1) newFeatures.add(RoomFeature.W_WALL);
        if (faceMissing == 2) newFeatures.add(RoomFeature.S_WALL);
        if (faceMissing == 3) newFeatures.add(RoomFeature.N_WALL);
        if (faceMissing == 4) newFeatures.add(RoomFeature.E_WALL);
        if (faceMissing == 5) newFeatures.add(RoomFeature.NO_CEILING);
        if (faceMissing == 6) newFeatures.add(RoomFeature.NO_FLOOR);*/

        int structureSpecial = random.nextInt(5);
        if (structureSpecial == 1) newFeatures.add(RoomFeature.PILLARS);

        int superDeep = 0;
        int superWide = 0;
        int superLong = 0;
        int randWidth = (random.nextInt(RoomsConfig.get().sizeVariation.x + 1) - RoomsConfig.get().sizeVariationShift.x) * RoomsConfig.get().sizeVariationFactor.x;
        int randLength = (random.nextInt(RoomsConfig.get().sizeVariation.z + 1) - RoomsConfig.get().sizeVariationShift.z) * RoomsConfig.get().sizeVariationFactor.z;
        int randHeight = (random.nextInt(RoomsConfig.get().sizeVariation.y + 1) - RoomsConfig.get().sizeVariationShift.y) * RoomsConfig.get().sizeVariationFactor.y;
        //if (random.nextInt(101) == 100) superDeep = 40;
        //if (random.nextInt(101) == 100) superLong = 30;
        //if (random.nextInt(101) == 100) superWide = 30;

        int pipeRoom = random.nextInt(5);
        if (pipeRoom == 1) {
            newFeatures.clear();
            newFeatures.addAll(List.of(RoomFeature.BASEBRICKS, RoomFeature.BASESTONE, RoomFeature.HARD_FLOOR, RoomFeature.HARD_CEILING, RoomFeature.LARGE_BRICKS, RoomFeature.E_PIPES, RoomFeature.E_CHAINS));
            randHeight = 3;
            randLength = 25;
            randWidth = -2;
        }

        Size newSize = new Size(RoomsConfig.get().defaultRoomSize.x + randWidth + superWide, RoomsConfig.get().defaultRoomSize.y + randHeight + superDeep, RoomsConfig.get().defaultRoomSize.z + randLength + superLong);

        if (canFit(pos, newSize)) return new Room(pos, newSize, newFeatures);
        else return null;
    }

    private static void createRoom(Room room, Level level) {

        if (!RoomsConfig.load().roomBlocking) RoomsController.newRooms.add(room);
        else {

            int xCut = 0;
            //int yCut = 0;
            int zCut = 0;

            int i = room.size.x;
            while (i > RoomsConfig.get().maxRoomSize.x) {
                xCut++;
                i -= RoomsConfig.get().maxRoomSize.x;
            }

            /*i = room.size.y;
            while (i > RoomsConfig.get().maxRoomSize.y) {
               yCut++;
                i -= RoomsConfig.get().maxRoomSize.y;
            }*/

            i = room.size.z;
            while (i > RoomsConfig.get().maxRoomSize.z) {
                zCut++;
                i -= RoomsConfig.get().maxRoomSize.z;
            }

            if (xCut > 0 || zCut > 0) {
                Size cut = new Size(xCut, 0, zCut);
                List<Room> newRooms = cutRoom(cut, room, new ArrayList<>());
                for (Room newRoom : newRooms) level.setBlock(newRoom.origin, RoomFeature.FLOOR.getState(), 3);
                RoomsController.newRooms.addAll(newRooms);
            } else {
                level.setBlock(room.origin, RoomFeature.FLOOR.getState(), 3);
                RoomsController.newRooms.add(room);
            }

        }
    }

    private static List<Room> cutRoom(Size cut, Room room, List<Room> rooms) {

        if (cut.x >= 1 && cut.z >= 1) {
            rooms.add(new Room(room.origin, new Size(RoomsConfig.get().maxRoomSize.x, room.size.y, RoomsConfig.get().maxRoomSize.z), new ArrayList<>(Stream.concat(Stream.of(RoomFeature.N_WALL, RoomFeature.W_WALL), room.features.stream()).toList())));

            Room newRoomX = new Room(new BlockPos(room.origin.getX() - RoomsConfig.get().maxRoomSize.x, room.origin.getY(), room.origin.getZ()), new Size(room.size.x - RoomsConfig.get().maxRoomSize.x, room.size.y, RoomsConfig.get().maxRoomSize.z), new ArrayList<>(Stream.concat(Stream.of(RoomFeature.E_WALL, RoomFeature.N_WALL), room.features.stream()).toList()));
            Room newRoomZ = new Room(new BlockPos(room.origin.getX(), room.origin.getY(), room.origin.getZ() - RoomsConfig.get().maxRoomSize.z), new Size(RoomsConfig.get().maxRoomSize.x, room.size.y, room.size.z - RoomsConfig.get().maxRoomSize.z), new ArrayList<>(Stream.concat(Stream.of(RoomFeature.S_WALL, RoomFeature.W_WALL), room.features.stream()).toList()));
            Room newRoom = new Room(new BlockPos(room.origin.getX() - RoomsConfig.get().maxRoomSize.x, room.origin.getY(), room.origin.getZ() - RoomsConfig.get().maxRoomSize.z), new Size(room.size.x - RoomsConfig.get().maxRoomSize.x, room.size.y, room.size.z - RoomsConfig.get().maxRoomSize.z), new ArrayList<>(Stream.concat(Stream.of(RoomFeature.N_WALL, RoomFeature.E_WALL), room.features.stream()).toList()));

            if (cut.x == 1 && cut.z == 1) rooms.addAll(List.of(newRoomX, newRoomZ, newRoom));
            else {
                rooms.addAll(cutRoom(new Size(cut.x - 1, 0, 0), newRoomX, new ArrayList<>()));
                rooms.addAll(cutRoom(new Size(0, 0, cut.z - 1), newRoomZ, new ArrayList<>()));
                rooms.addAll(cutRoom(new Size(cut.x - 1, 0, cut.z - 1), newRoom, new ArrayList<>()));
            }
        }

        if (cut.x >= 1 && cut.z == 0) {
            rooms.add(new Room(room.origin, new Size(RoomsConfig.get().maxRoomSize.x, room.size.y, room.size.z), new ArrayList<>(Stream.concat(Stream.of(RoomFeature.E_WALL), room.features.stream()).toList())));

            Room newRoom = new Room(new BlockPos(room.origin.getX() - RoomsConfig.get().maxRoomSize.x, room.origin.getY(), room.origin.getZ()), new Size(room.size.x - RoomsConfig.get().maxRoomSize.x, room.size.y, room.size.z), new ArrayList<>(Stream.concat(Stream.of(RoomFeature.W_WALL), room.features.stream()).toList()));

            if (cut.x == 1) rooms.add(newRoom);
            else rooms.addAll(cutRoom(new Size(cut.x -1, 0, 0), newRoom, new ArrayList<>()));


        }

        if (cut.x == 0 && cut.z >= 1) {
            rooms.add(new Room(room.origin, new Size(room.size.x, room.size.y, RoomsConfig.get().maxRoomSize.z), new ArrayList<>(Stream.concat(Stream.of(RoomFeature.N_WALL), room.features.stream()).toList())));

            Room newRoom = new Room(new BlockPos(room.origin.getX(), room.origin.getY(), room.origin.getZ() - RoomsConfig.get().maxRoomSize.z), new Size(room.size.x, room.size.y, room.size.z - RoomsConfig.get().maxRoomSize.z), new ArrayList<>(Stream.concat(Stream.of(RoomFeature.S_WALL), room.features.stream()).toList()));

            if (cut.z == 1) rooms.add(newRoom);
            else rooms.addAll(cutRoom(new Size(0, 0, cut.z - 1), newRoom, new ArrayList<>()));


        }

        return rooms;

    }

    private BlockPosStateBoolTriple search(Level level, BlockPos pos, List<BlockPos> searchedBlocks, boolean createRooms) {

        List<Direction> tempDirections = new ArrayList<Direction>(RoomsController.directions);
        Collections.shuffle(tempDirections);
        BlockPos potentialPos;

        for (Direction dir : tempDirections) {

            potentialPos = pos.relative(dir);

            if (isWithinBounds(potentialPos) && !searchedBlocks.contains(potentialPos)) {

                if (contains(potentialPos)) {

                    if (getStateAt(potentialPos).is(Blocks.AIR) && level.getBlockState(potentialPos).is(Blocks.WATER)) { searchedBlocks.add(pos); return search(level, potentialPos, searchedBlocks, createRooms); }

                    if (!level.getBlockState(potentialPos).is(getStateAt(potentialPos).getBlock())) {

                        return new BlockPosStateBoolTriple(potentialPos, getStateAt(potentialPos), false);

                    } else {
                        searchedBlocks.add(pos);
                        return search(level, potentialPos, searchedBlocks, createRooms);
                    }

                } else if (!contains(potentialPos) && createRooms && canFit(potentialPos, RoomsConfig.get().defaultRoomSize)) {
                    if (RoomsConfig.get().generateOnPlane) {
                        if (RoomsController.roomsPlanes.contains(potentialPos.getY())) return new BlockPosStateBoolTriple(potentialPos, RoomFeature.FLOOR.getState(), true);
                        for (Integer plane : RoomsController.roomsPlanes) {
                            if (!(potentialPos.getY() < plane + RoomsConfig.get().defaultRoomSize.y) && !(potentialPos.getY() > plane - RoomsConfig.get().defaultRoomSize.y)) return new BlockPosStateBoolTriple(potentialPos, RoomFeature.FLOOR.getState(), true);
                        }
                    }
                    return new BlockPosStateBoolTriple(potentialPos, RoomFeature.FLOOR.getState(), true);
                }

            }

        }

        return null;

    }

    private boolean contains(BlockPos pos) {

        if (pos.getX() > origin.getX()
            || pos.getX() < origin.getX() - size.x
            || pos.getZ() > origin.getZ()
            || pos.getZ() < origin.getZ() - size.z
            || pos.getY() < origin.getY()
            || pos.getY() > origin.getY() + size.y) return false;

        return true;
    }

    public boolean isComplete(Level level) {

        for (BlockPos pos : BlockPos.betweenClosed(
                origin,
                origin.offset(size.x*-1, size.y-1, size.z*-1))) {

            if (!level.getBlockState(pos).is(getStateAt(pos.immutable()).getBlock())) {
                if (!RoomsConfig.get().strictGeneration && getStateAt(pos.immutable()).is(Blocks.AIR)) return true;
                else return false;
                //Spreadrooms.LOGGER.info("FALSE " + pos + " " + level.getBlockState(pos));
            }
        }

        return true;
    }

    private BlockState getStateAt(BlockPos pos) {

        if (!features.isEmpty()) {

            if (features.contains(RoomFeature.LIGHT) && !features.contains(RoomFeature.NO_CEILING) && pos.getX() == (origin.getX() - (size.x/2)) && pos.getZ() == (origin.getZ() - (size.z/2)) && pos.getY() == origin.getY() + size().y) {
                return RoomFeature.LIGHT.getState();
            }

            if ((features.contains(RoomFeature.PILLARS) || features.contains(RoomFeature.MULTILIGHT)) && !(pos.getX() == origin.getX()) && !(pos.getX() == origin.getX() - size.x) && !(pos.getZ() == origin.getZ()) && !(pos.getZ() == origin.getZ() - size.z) && (pos.getX() - origin.getX()) % 2  == 0 && (pos.getZ() - origin.getZ()) % 2  == 0) {
                if (features.contains(RoomFeature.PILLARS)) return RoomFeature.PILLARS.getState();
                if (pos.getY() == origin.getY() + size.y && !features.contains(RoomFeature.NO_CEILING)) return RoomFeature.MULTILIGHT.getState();
            }

        }

        if (pos.getY() == origin.getY()) {
            if (!features.isEmpty()) {
                if (features.contains(RoomFeature.NO_FLOOR)) return RoomFeature.NO_FLOOR.getState();
                if (features.contains(RoomFeature.HARD_FLOOR)) return RoomFeature.HARD_FLOOR.getState();
                if (features.contains(RoomFeature.CARPET)) return RoomFeature.CARPET.getDefaultState();
            }
            return RoomFeature.FLOOR.getState();
        }

        if (pos.getY() == origin.getY() + size.y) {
            if (!features.isEmpty()) {
                if (features.contains(RoomFeature.NO_CEILING)) return RoomFeature.NO_CEILING.getState();
                if (features.contains(RoomFeature.HARD_CEILING)) return RoomFeature.HARD_CEILING.getState();
            }
            return RoomFeature.CEILING.getState();
        }

        if (!features.isEmpty()) {
            if (features.contains(RoomFeature.E_PIPES) && pos.getX() == origin.getX() - 1) {
                if (pos.getY() == origin.getY() + 2) return RoomFeature.E_PIPES.getDefaultState();
                if (pos.getY() == origin.getY() + 3 && pos.getZ() == origin.getZ()) return RoomFeature.E_PIPES.getDefaultState();
            }
        }

        if (RoomsConfig.get().connectRooms && !((pos.getX() == origin.getX() - size.x && pos.getZ() == origin.getZ()) || (pos.getX() == origin.getX() && pos.getZ() == origin.getZ() - size.z) || (pos.getX() == origin.getX() && pos.getZ() == origin.getZ()) || (pos.getX() == origin.getX() - size.x && pos.getZ() == origin.getZ() - size.z))) {
            List<BlockPos> neighborBlocks = new ArrayList<>(getNeighborBlocks());
            if (!neighborBlocks.isEmpty() && neighborBlocks.contains(pos)) return RoomFeature.NONE.getState();
        }

        if ((pos.getZ() == origin.getZ() || pos.getZ() == origin.getZ() - size.z) || (pos.getX() == origin.getX() || pos.getX() == origin.getX() - size.x)) {
            if (!features.isEmpty()) {
                if (features.contains(RoomFeature.BASEBOARDS) && pos.getY() == origin.getY() + 1) return RoomFeature.BASEBOARDS.getState();
                if (features.contains(RoomFeature.BASESTONE) && (pos.getY() == origin.getY() + 1 || pos.getY() == origin.getY() + size.y - 1)) return RoomFeature.BASESTONE.getState();
                if (features.contains(RoomFeature.BASEBRICKS) && (pos.getY() == origin.getY() + 2 || pos.getY() == origin.getY() + size.y - 2)) return RoomFeature.BASEBRICKS.getState();
                if (features.contains(RoomFeature.MOSAIC)) return RoomFeature.MOSAIC.getState();
                if (features.contains(RoomFeature.BAMBOO)) return RoomFeature.BAMBOO.getState();
                if (features.contains(RoomFeature.GREEN)) return RoomFeature.GREEN.getDefaultState();
                if (features.contains(RoomFeature.SMOOTH)) return RoomFeature.SMOOTH.getState();
                if (features.contains(RoomFeature.LARGE_BRICKS)) return RoomFeature.LARGE_BRICKS.getState();

                //remove corners from the empty walls
                if (!((pos.getX() == origin.getX() - size.x && pos.getZ() == origin.getZ()) || (pos.getX() == origin.getX() && pos.getZ() == origin.getZ() - size.z) || (pos.getX() == origin.getX() && pos.getZ() == origin.getZ()) || (pos.getX() == origin.getX() - size.x && pos.getZ() == origin.getZ() - size.z))) {
                    if (features.contains(RoomFeature.S_WALL) && pos.getZ() == origin.getZ()) return RoomFeature.S_WALL.getState();
                    if (features.contains(RoomFeature.E_WALL) && pos.getX() == origin.getX()) return RoomFeature.E_WALL.getState();
                    if (features.contains(RoomFeature.W_WALL) && pos.getX() == origin.getX() - size.z) return RoomFeature.W_WALL.getState();
                    if (features.contains(RoomFeature.N_WALL) && pos.getZ() == origin.getZ() - size.x) return RoomFeature.N_WALL.getState();
                }
            }

            return RoomFeature.WALL.getState();
        }

        if (!features.isEmpty()) {
            if ((features.contains(RoomFeature.E_CHAINS) && pos.getX() == origin.getX() - 1 && pos.getY() == origin.getY() + 3)) return RoomFeature.E_CHAINS.getDefaultState();
        }

        return RoomFeature.NONE.getState();

    }

    private List<BlockPos> getNeighborBlocks() {

        List<BlockPos> neighborBlocks = new ArrayList<>();

        List<Room> allRooms = new ArrayList<>();
        allRooms.addAll(RoomsController.rooms);
        allRooms.addAll(RoomsController.newRooms);
        allRooms.addAll(RoomsController.completeRooms);
        for (Room room : RoomsController.rooms) {

            for (BlockPos pos : BlockPos.betweenClosed(origin, origin.offset(size.x*-1, size.y-1, 0))) {
                for (Room allRoom : allRooms) { if (room != allRoom && allRoom.contains(pos.relative(Direction.SOUTH))) neighborBlocks.add(pos.immutable()); }
            }
            for (BlockPos pos : BlockPos.betweenClosed(origin, origin.offset(0, size.y-1, size.z*-1))) {
                for (Room allRoom : allRooms) { if (room != allRoom && allRoom.contains(pos.relative(Direction.EAST))) neighborBlocks.add(pos.immutable()); }
            }
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(0, 0, size.z*-1), origin.offset(size.x*-1, size.y-1, size.z*-1))) {
                for (Room allRoom : allRooms) { if (room != allRoom && allRoom.contains(pos.relative(Direction.NORTH))) neighborBlocks.add(pos.immutable()); }
            }
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(size.x*-1, 0, 0), origin.offset(size.x*-1, size.y-1, size.z*-1))) {
                for (Room allRoom : allRooms) { if (room != allRoom && allRoom.contains(pos.relative(Direction.WEST))) neighborBlocks.add(pos.immutable()); }
            }

        }

        return neighborBlocks;
    }

    private static boolean canFit(BlockPos origin, Size roomSize) {

        List<Room> allRooms = new ArrayList<>();
        allRooms.addAll(RoomsController.rooms);
        allRooms.addAll(RoomsController.newRooms);
        allRooms.addAll(RoomsController.completeRooms);

        for (Room room : allRooms) {

            for (BlockPos pos : BlockPos.betweenClosed(
                    origin,
                    origin.offset(roomSize.x*-1, roomSize.y, roomSize.z*-1))) {

                if (room.contains(pos.immutable()) || !isWithinBounds(pos.immutable())) {
                    return false;
                }
            }

        }

        return true;
    }

    private static boolean isWithinBounds(BlockPos pos) {

        if (pos.getX() < (RoomsController.center.getX() - RoomsConfig.get().areaFromCenter - 1)
                || pos.getX() > (RoomsController.center.getX() + RoomsConfig.get().areaFromCenter + 1)
                || pos.getZ() < (RoomsController.center.getZ() - RoomsConfig.get().areaFromCenter - 1)
                || pos.getZ() > (RoomsController.center.getZ() + RoomsConfig.get().areaFromCenter + 1)
                || pos.getY() < -64) return false;

        return true;
    }

}
