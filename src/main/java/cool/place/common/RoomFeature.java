package cool.place.common;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;

public enum RoomFeature {
    WALL(Blocks.STRIPPED_BIRCH_LOG.defaultBlockState()),
    FLOOR(Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState()),
    CEILING(Blocks.SMOOTH_STONE.defaultBlockState()),
    //Lights
    LIGHT(Blocks.OCHRE_FROGLIGHT.defaultBlockState()),
    MULTILIGHT(Blocks.OCHRE_FROGLIGHT.defaultBlockState()),
    //Structures
    PILLARS(Blocks.STRIPPED_BIRCH_LOG.defaultBlockState()),
    E_PIPES(Blocks.COPPER_BLOCK.waxed().exposed().defaultBlockState()),
    E_CHAINS(Blocks.COPPER_CHAIN.waxed().exposed().defaultBlockState().setValue(ChainBlock.AXIS, Direction.Axis.Z)),
    //Floor Variations
    HARD_FLOOR(Blocks.STONE.defaultBlockState()),
    CARPET(Blocks.WOOL.brown().defaultBlockState()),
    //Ceiling Variations
    HARD_CEILING(Blocks.STONE.defaultBlockState()),
    //Wall Variations
    MOSAIC(Blocks.BAMBOO_MOSAIC.defaultBlockState()),
    BAMBOO(Blocks.STRIPPED_BAMBOO_BLOCK.defaultBlockState()),
    SMOOTH(Blocks.SMOOTH_SANDSTONE.defaultBlockState()),
    GREEN(Blocks.DYED_TERRACOTTA.green().defaultBlockState()),
    BASEBOARDS(Blocks.BIRCH_PLANKS.defaultBlockState()),
    BASEBRICKS(Blocks.TUFF_BRICKS.defaultBlockState()),
    BASESTONE(Blocks.SMOOTH_BASALT.defaultBlockState()),
    LARGE_BRICKS(Blocks.SMOOTH_STONE.defaultBlockState()),
    DOOR(Blocks.AIR.defaultBlockState()),
    //Missing faces
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

    public BlockState getState() { return BuiltInRegistries.BLOCK
            .get(Identifier.parse(RoomsConfig.get().featureStates.get(this)))
            .map(Holder.Reference::value)
            .orElseThrow()
            .defaultBlockState(); }

    public BlockState getDefaultState() {
        return state;
    }

}
