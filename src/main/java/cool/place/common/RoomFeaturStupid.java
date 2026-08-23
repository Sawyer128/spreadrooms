package cool.place.common;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class RoomFeaturStupid {

    private static final Map<String, featureData> roomFeatures = new HashMap<>();

    private record featureData(RoomFeatureType type, BlockState state) {}

    static {
        //Adding all default features to roomFeatures
        //Defaults
        addFeat("WALL", RoomFeatureType.WALL, Blocks.STRIPPED_BIRCH_LOG.defaultBlockState());
        addFeat("FLOOR", RoomFeatureType.FLOOR, Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState());
        addFeat("CEILING", RoomFeatureType.CEILING,Blocks.SMOOTH_STONE.defaultBlockState());
        //Structures
        addFeat("PILLARS", RoomFeatureType.STRUCTURE, Blocks.STRIPPED_BIRCH_LOG.defaultBlockState());
        addFeat("E_PIPES", RoomFeatureType.STRUCTURE, Blocks.COPPER_BLOCK.waxed().exposed().defaultBlockState());
        addFeat("E_CHAINS", RoomFeatureType.STRUCTURE, Blocks.COPPER_CHAIN.waxed().exposed().defaultBlockState().setValue(ChainBlock.AXIS, Direction.Axis.Z));
        //Floors
        addFeat("HARD_FLOOR", RoomFeatureType.FLOOR, Blocks.STONE.defaultBlockState());
        addFeat("CARPET", RoomFeatureType.FLOOR, Blocks.WOOL.brown().defaultBlockState());
        //Ceilings
        addFeat("HARD_CEILING", RoomFeatureType.CEILING, Blocks.STONE.defaultBlockState());
        addFeat("YELLOW_CEILING", RoomFeatureType.CEILING, Blocks.POLISHED_SULFUR.defaultBlockState());
        //Ceiling parts
        addFeat("LIGHT", RoomFeatureType.CEILING_PART, Blocks.OCHRE_FROGLIGHT.defaultBlockState());
        addFeat("MULTILIGHT", RoomFeatureType.CEILING_PART, Blocks.OCHRE_FROGLIGHT.defaultBlockState());
        //Walls
        addFeat("MOSAIC", RoomFeatureType.WALL, Blocks.BAMBOO_MOSAIC.defaultBlockState());
        addFeat("YELLOW", RoomFeatureType.WALL, Blocks.SULFUR.defaultBlockState());
        addFeat("BAMBOO", RoomFeatureType.WALL, Blocks.STRIPPED_BAMBOO_BLOCK.defaultBlockState());
        addFeat("SMOOTH", RoomFeatureType.WALL, Blocks.SMOOTH_SANDSTONE.defaultBlockState());
        addFeat("GREEN", RoomFeatureType.WALL, Blocks.DYED_TERRACOTTA.green().defaultBlockState());
        addFeat("LARGE_BRICKS", RoomFeatureType.WALL_PART, Blocks.SMOOTH_STONE.defaultBlockState());
        //Wall parts
        addFeat("BIRCH_BASEBOARD", RoomFeatureType.WALL_PART, Blocks.BIRCH_PLANKS.defaultBlockState());
        addFeat("BASE_BRICK", RoomFeatureType.WALL_PART, Blocks.TUFF_BRICKS.defaultBlockState());
        addFeat("BASE_STONE", RoomFeatureType.WALL_PART, Blocks.SMOOTH_BASALT.defaultBlockState());
        //Other
        addFeat("WEST_EMPTY", RoomFeatureType.WALL, Blocks.AIR.defaultBlockState());
        addFeat("SOUTH_EMPTY", RoomFeatureType.WALL, Blocks.AIR.defaultBlockState());
        addFeat("EAST_EMPTY", RoomFeatureType.WALL, Blocks.AIR.defaultBlockState());
        addFeat("NORTH_EMPTY", RoomFeatureType.WALL, Blocks.AIR.defaultBlockState());
        addFeat("CEILING_EMPTY", RoomFeatureType.CEILING, Blocks.AIR.defaultBlockState());
        addFeat("FLOOR_EMPTY", RoomFeatureType.FLOOR, Blocks.AIR.defaultBlockState());
    }

    public static void addFeat(String name, RoomFeatureType type, BlockState state) {
        roomFeatures.put(name, new featureData(type, state));
    }

    public static RoomFeatureType getType(String name) {
        return roomFeatures.get(name).type;
    }

    public static BlockState getState(String name) {
        return roomFeatures.get(name).state;
    }




}
