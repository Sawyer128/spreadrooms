package cool.place.common;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;

public enum RoomFeatureEnum {
    WALL(RoomFeatureType.WALL, Blocks.STRIPPED_BIRCH_LOG.defaultBlockState()),
    FLOOR(RoomFeatureType.FLOOR, Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState()),
    CEILING(RoomFeatureType.CEILING,Blocks.SMOOTH_STONE.defaultBlockState()),
    //Lights
    LIGHT(RoomFeatureType.CEILING_PART, Blocks.OCHRE_FROGLIGHT.defaultBlockState()),
    MULTILIGHT(RoomFeatureType.CEILING_PART, Blocks.OCHRE_FROGLIGHT.defaultBlockState()),
    //Structures
    PILLARS(RoomFeatureType.STRUCTURE, Blocks.STRIPPED_BIRCH_LOG.defaultBlockState()),
    E_PIPES(RoomFeatureType.STRUCTURE, Blocks.COPPER_BLOCK.waxed().exposed().defaultBlockState()),
    E_CHAINS(RoomFeatureType.STRUCTURE, Blocks.COPPER_CHAIN.waxed().exposed().defaultBlockState().setValue(ChainBlock.AXIS, Direction.Axis.Z)),
    //Floor Variations
    HARD_FLOOR(RoomFeatureType.FLOOR, Blocks.STONE.defaultBlockState()),
    CARPET(RoomFeatureType.FLOOR, Blocks.WOOL.brown().defaultBlockState()),
    //Ceiling Variations
    HARD_CEILING(RoomFeatureType.CEILING, Blocks.STONE.defaultBlockState()),
    YELLOW_CEILING(RoomFeatureType.CEILING, Blocks.POLISHED_SULFUR.defaultBlockState()),
    //Wall Variations
    MOSAIC(RoomFeatureType.WALL, Blocks.BAMBOO_MOSAIC.defaultBlockState()),
    YELLOW(RoomFeatureType.WALL, Blocks.SULFUR.defaultBlockState()),
    BAMBOO(RoomFeatureType.WALL, Blocks.STRIPPED_BAMBOO_BLOCK.defaultBlockState()),
    SMOOTH(RoomFeatureType.WALL, Blocks.SMOOTH_SANDSTONE.defaultBlockState()),
    GREEN(RoomFeatureType.WALL, Blocks.DYED_TERRACOTTA.green().defaultBlockState()),
    BASEBOARDS(RoomFeatureType.WALL_PART, Blocks.BIRCH_PLANKS.defaultBlockState()),
    BASEBRICKS(RoomFeatureType.WALL_PART, Blocks.TUFF_BRICKS.defaultBlockState()),
    BASESTONE(RoomFeatureType.WALL_PART, Blocks.SMOOTH_BASALT.defaultBlockState()),
    LARGE_BRICKS(RoomFeatureType.WALL_PART, Blocks.SMOOTH_STONE.defaultBlockState()),
    DOOR(RoomFeatureType.WALL_PART, Blocks.AIR.defaultBlockState()),
    //Missing faces
    W_WALL(RoomFeatureType.WALL, Blocks.AIR.defaultBlockState()),
    S_WALL(RoomFeatureType.WALL, Blocks.AIR.defaultBlockState()),
    N_WALL(RoomFeatureType.WALL, Blocks.AIR.defaultBlockState()),
    E_WALL(RoomFeatureType.WALL, Blocks.AIR.defaultBlockState()),
    NO_CEILING(RoomFeatureType.CEILING, Blocks.AIR.defaultBlockState()),
    NO_FLOOR(RoomFeatureType.FLOOR, Blocks.AIR.defaultBlockState()),
    NONE(RoomFeatureType.EMPTY, Blocks.AIR.defaultBlockState());

    private final BlockState state;
    private final RoomFeatureType type;

    RoomFeatureEnum(RoomFeatureType type, BlockState state) {
        this.state = state;
        this.type = type;
    }

    public BlockState getState() { return BuiltInRegistries.BLOCK
            .get(Identifier.parse(RoomsConfig.get().featureStates.get(this)))
            .map(Holder.Reference::value)
            .orElseThrow()
            .defaultBlockState(); }

    public BlockState getDefaultState() {
        return state;
    }

    public RoomFeatureType getType() {
        return type;
    }

}
