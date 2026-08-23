package cool.place.common;

import java.util.List;

public enum RoomFeatureSetEnum {
    DEFAULT(
            List.of(RoomFeatureEnum.WALL, RoomFeatureEnum.FLOOR, RoomFeatureEnum.CEILING),
            List.of(new randomFeature(RoomFeatureEnum.PILLARS, 20), new randomFeature(RoomFeatureEnum.CARPET, 20), new randomFeature(RoomFeatureEnum.LIGHT, 50), new randomFeature(RoomFeatureEnum.MULTILIGHT, 25), new randomFeature(RoomFeatureEnum.BAMBOO, 25), new randomFeature(RoomFeatureEnum.SMOOTH, 50), new randomFeature(RoomFeatureEnum.MOSAIC, 5), new randomFeature(RoomFeatureEnum.GREEN, 5)),
            new Room.Size(0, 0 , 0),
            98),
    EAST_PIPES_HALLWAY(
            List.of(RoomFeatureEnum.BASEBRICKS, RoomFeatureEnum.BASESTONE, RoomFeatureEnum.HARD_FLOOR, RoomFeatureEnum.HARD_CEILING, RoomFeatureEnum.LARGE_BRICKS, RoomFeatureEnum.E_PIPES, RoomFeatureEnum.E_CHAINS),
            List.of(),
            new Room.Size(-2, 3 , 25),
            1),
    SULFUR_ROOM(
            List.of(RoomFeatureEnum.YELLOW, RoomFeatureEnum.YELLOW_CEILING),
            List.of(),
            new Room.Size(0, 0 , 0),
            1);

    public record randomFeature(RoomFeatureEnum feature, int featureChance) {}

    private final List<RoomFeatureEnum> features;
    private final List<randomFeature> alternates;
    private final Room.Size sizeShift;
    private final int chance;

    RoomFeatureSetEnum(List<RoomFeatureEnum> features, List<randomFeature> alternates, Room.Size sizeShift, int chance) {
        this.features = features;
        this.alternates = alternates;
        this.sizeShift = sizeShift;
        this.chance = chance;
    }

    public List<RoomFeatureEnum> getFeatures() { return features; }
    public List<randomFeature> getAlternates() { return alternates; }
    public Room.Size getSizeShift() { return sizeShift; }
    public int getChance() { return chance; }


}
