package cool.place.common;

import cool.place.Spreadrooms;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public record Room(BlockPos origin, Size size, List<RoomFeature> features) {

    public record Size (int x, int y, int z) { }

    private record BlockPosStateBoolTriple(BlockPos pos, BlockState state, boolean outside) {}

    public void spread(Level level, boolean createRooms) {

        BlockPosStateBoolTriple result = search(level, origin, new ArrayList<>(), createRooms);

        if (result != null) {

            if (!result.outside) {
                level.setBlock(result.pos, result.state, 3);
            } else {
                createRoom(level, result.pos, result.state);
            }
        }
    }

    private void createRoom(Level level, BlockPos pos, BlockState state) {

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

        int faceMissing = random.nextInt(20);
        if (faceMissing == 1) newFeatures.add(RoomFeature.W_WALL);
        if (faceMissing == 2) newFeatures.add(RoomFeature.S_WALL);
        if (faceMissing == 3) newFeatures.add(RoomFeature.N_WALL);
        if (faceMissing == 4) newFeatures.add(RoomFeature.E_WALL);
        if (faceMissing == 5) newFeatures.add(RoomFeature.NO_CEILING);
        if (faceMissing == 6) newFeatures.add(RoomFeature.NO_FLOOR);

        int structureSpecial = random.nextInt(5);
        if (structureSpecial == 1) newFeatures.add(RoomFeature.PILLARS);

        int openingSpecial = random.nextInt(2);

        int superDeep = 0;
        int randWidth = (random.nextInt(5) - 1) * 2;
        int randLength = (random.nextInt(5) - 1) * 2;
        if (random.nextInt(101) > 99) superDeep = 40;
        Size newSize = new Size(RoomsConfig.get().defaultRoomSize.x + randWidth, RoomsConfig.get().defaultRoomSize.y + superDeep, RoomsConfig.get().defaultRoomSize.z + randLength);

        Room newRoom = new Room(pos, newSize, newFeatures);
        if (canFit(pos, newSize)) {
            level.setBlock(pos, state, 3);
            RoomsController.newRooms.add(newRoom);
        }

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

                } else if (!contains(potentialPos) && createRooms && canFit(potentialPos, RoomsController.defaultSize)) {

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

            if (!level.getBlockState(pos).is(getStateAt(pos.immutable()).getBlock()) && !getStateAt(pos.immutable()).is(Blocks.AIR)) {
                return false;
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

            if (features.contains(RoomFeature.BASEBOARDS) && ((pos.getX() == origin.getX()) || (pos.getX() == origin.getX() - size.x) || (pos.getZ() == origin.getZ()) || (pos.getZ() == origin.getZ() - size.z)) && pos.getY() == origin.getY() + 1) {
                return RoomFeature.BASEBOARDS.getState();
            }

            if (features.contains(RoomFeature.S_WALL) && pos.getX() == origin.getX()) {
                return RoomFeature.S_WALL.getState();
            }

            if (features.contains(RoomFeature.E_WALL) && pos.getZ() == origin.getZ()) {
                return RoomFeature.E_WALL.getState();
            }

            if (features.contains(RoomFeature.W_WALL) && pos.getZ() == origin.getZ() - size.z) {
                return RoomFeature.W_WALL.getState();
            }

            if (features.contains(RoomFeature.N_WALL) && pos.getX() == origin.getX() - size.x) {
                return RoomFeature.N_WALL.getState();
            }

        }

        if (pos.getY() == origin.getY()) {
            if (!features.isEmpty()) {
                if (features.contains(RoomFeature.NO_FLOOR)) return RoomFeature.NO_FLOOR.getState();
            }
            return RoomFeature.FLOOR.getState();
        }

        if ( (pos.getZ() == origin.getZ() || pos.getZ() == origin.getZ() - size.z) || (pos.getX() == origin.getX() || pos.getX() == origin.getX() - size.x)) {
            if (!features.isEmpty()) {
                if (features.contains(RoomFeature.MOSAIC)) return RoomFeature.MOSAIC.getState();
                if (features.contains(RoomFeature.BAMBOO)) return RoomFeature.BAMBOO.getState();
                if (features.contains(RoomFeature.GREEN)) return RoomFeature.GREEN.getState();
                if (features.contains(RoomFeature.SMOOTH)) return RoomFeature.SMOOTH.getState();
            }
            return RoomFeature.WALL.getState();
        }

        if (pos.getY() == origin.getY() + size.y) {
            if (!features.isEmpty()) {
                if (features.contains(RoomFeature.NO_CEILING)) return RoomFeature.NO_CEILING.getState();
            }
            return RoomFeature.CEILING.getState();
        }

        return Blocks.AIR.defaultBlockState();

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
