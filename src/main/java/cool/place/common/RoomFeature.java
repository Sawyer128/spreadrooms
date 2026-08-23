package cool.place.common;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record RoomFeature(String name, RoomFeatureType type, BlockState state) {

    private static final List<RoomFeature> features = new ArrayList<>();

    static {
        //add("EMPTY", RoomFeatureType.EMPTY, Blocks.AIR.defaultBlockState());
        addDefaults();
    }

    public static RoomFeature get(String name) {
        for (RoomFeature feature : features) {
            if (Objects.equals(feature.name, name)) return feature;
        }
        return new RoomFeature("EMPTY", RoomFeatureType.EMPTY, Blocks.AIR.defaultBlockState());
    }

    public static List<RoomFeature> getAll() { return features; }
    
    public static void add(RoomFeature feature) { features.add(feature); }

    public static void add(String name, RoomFeatureType type, BlockState state) { features.add(new RoomFeature(name, type, state)); }
    
    public static void addDefaults() {
        //Adding all default features to roomFeatures
        //Defaults
        add("WALL", RoomFeatureType.WALL, Blocks.STRIPPED_BIRCH_LOG.defaultBlockState());
        add("FLOOR", RoomFeatureType.FLOOR, Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState());
        add("CEILING", RoomFeatureType.CEILING,Blocks.SMOOTH_STONE.defaultBlockState());
        //Structures
        add("PILLARS", RoomFeatureType.STRUCTURE, Blocks.STRIPPED_BIRCH_LOG.defaultBlockState());
        add("EAST_PIPES", RoomFeatureType.STRUCTURE, Blocks.COPPER_BLOCK.waxed().exposed().defaultBlockState());
        add("EAST_CHAINS", RoomFeatureType.STRUCTURE, Blocks.COPPER_CHAIN.waxed().exposed().defaultBlockState().setValue(ChainBlock.AXIS, Direction.Axis.Z));
        //Floors
        add("HARD_FLOOR", RoomFeatureType.FLOOR, Blocks.STONE.defaultBlockState());
        add("CARPET", RoomFeatureType.FLOOR, Blocks.WOOL.brown().defaultBlockState());
        //Ceilings
        add("HARD_CEILING", RoomFeatureType.CEILING, Blocks.STONE.defaultBlockState());
        add("YELLOW_CEILING", RoomFeatureType.CEILING, Blocks.POLISHED_SULFUR.defaultBlockState());
        //Ceiling parts
        add("LIGHT", RoomFeatureType.CEILING_PART, Blocks.OCHRE_FROGLIGHT.defaultBlockState());
        add("MULTILIGHT", RoomFeatureType.CEILING_PART, Blocks.OCHRE_FROGLIGHT.defaultBlockState());
        //Walls
        add("MOSAIC", RoomFeatureType.WALL, Blocks.BAMBOO_MOSAIC.defaultBlockState());
        add("YELLOW_WALLS", RoomFeatureType.WALL, Blocks.SULFUR.defaultBlockState());
        add("BAMBOO", RoomFeatureType.WALL, Blocks.STRIPPED_BAMBOO_BLOCK.defaultBlockState());
        add("SMOOTH", RoomFeatureType.WALL, Blocks.SMOOTH_SANDSTONE.defaultBlockState());
        add("GREEN", RoomFeatureType.WALL, Blocks.DYED_TERRACOTTA.green().defaultBlockState());
        add("LARGE_BRICKS", RoomFeatureType.WALL_PART, Blocks.SMOOTH_STONE.defaultBlockState());
        //Wall parts
        add("BIRCH_BASEBOARD", RoomFeatureType.WALL_PART, Blocks.BIRCH_PLANKS.defaultBlockState());
        add("BASE_BRICK", RoomFeatureType.WALL_PART, Blocks.TUFF_BRICKS.defaultBlockState());
        add("BASE_STONE", RoomFeatureType.WALL_PART, Blocks.SMOOTH_BASALT.defaultBlockState());
        //Other
        add("WEST_EMPTY", RoomFeatureType.WALL, Blocks.AIR.defaultBlockState());
        add("SOUTH_EMPTY", RoomFeatureType.WALL, Blocks.AIR.defaultBlockState());
        add("EAST_EMPTY", RoomFeatureType.WALL, Blocks.AIR.defaultBlockState());
        add("NORTH_EMPTY", RoomFeatureType.WALL, Blocks.AIR.defaultBlockState());
        add("CEILING_EMPTY", RoomFeatureType.CEILING, Blocks.AIR.defaultBlockState());
        add("FLOOR_EMPTY", RoomFeatureType.FLOOR, Blocks.AIR.defaultBlockState());
    }
    
}
