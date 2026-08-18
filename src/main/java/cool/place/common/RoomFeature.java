package cool.place.common;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public enum RoomFeature {
    WALL(Blocks.STRIPPED_BIRCH_LOG.defaultBlockState()),
    FLOOR(Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState()),
    CEILING(Blocks.SMOOTH_STONE.defaultBlockState()),
    LIGHT(Blocks.OCHRE_FROGLIGHT.defaultBlockState()),
    MULTILIGHT(Blocks.OCHRE_FROGLIGHT.defaultBlockState()),
    PILLARS(Blocks.STRIPPED_BIRCH_LOG.defaultBlockState()),
    MOSAIC(Blocks.BAMBOO_MOSAIC.defaultBlockState()),
    BAMBOO(Blocks.STRIPPED_BAMBOO_BLOCK.defaultBlockState()),
    SMOOTH(Blocks.SMOOTH_SANDSTONE.defaultBlockState()),
    GREEN(Blocks.DYED_TERRACOTTA.green().defaultBlockState()),
    BASEBOARDS(Blocks.BIRCH_PLANKS.defaultBlockState()),
    DOOR(Blocks.AIR.defaultBlockState()),
    W_WALL(Blocks.AIR.defaultBlockState()),
    S_WALL(Blocks.AIR.defaultBlockState()),
    N_WALL(Blocks.AIR.defaultBlockState()),
    E_WALL(Blocks.AIR.defaultBlockState()),
    NO_CEILING(Blocks.AIR.defaultBlockState()),
    NO_FLOOR(Blocks.AIR.defaultBlockState()),
    NONE(Blocks.AIR.defaultBlockState());

    private final BlockState state;

    RoomFeature(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

}
