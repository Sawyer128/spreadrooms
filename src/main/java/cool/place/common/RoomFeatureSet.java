package cool.place.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record RoomFeatureSet(String name, List<RoomFeature> features, List<RoomFeature> alternates, Room.Size sizeShift, int chance) {

    //public record randomFeature(RoomFeature feature, int featureChance) { }

    public static final List<RoomFeatureSet> featureSets = new ArrayList<>();

    static { addDefaults(); }

    public static RoomFeatureSet get(String name) {
        for (RoomFeatureSet featureSet : featureSets) {
            if (Objects.equals(featureSet.name, name)) return featureSet;
        }
        return null;
    }

    public static List<RoomFeatureSet> getAll() { return featureSets; }

    public List<RoomFeature> getAllAlternates() { return alternates; }


    public static void add(String name, List<RoomFeature> features, List<RoomFeature> alternates, Room.Size sizeShift, int chance) {
        featureSets.add(new RoomFeatureSet(name, features, alternates, sizeShift, chance));
    }

    public static void add(RoomFeatureSet featureSet) { featureSets.add(featureSet); }

    public static void addDefaults() {
        add(
                "DEFAULT",
                List.of(RoomFeature.get("WALL"), RoomFeature.get("FLOOR"), RoomFeature.get("CEILING")),
                List.of(RoomFeature.get("PILLARS"), RoomFeature.get("CARPET"), RoomFeature.get("LIGHT"), RoomFeature.get("MULTILIGHT"),RoomFeature.get("BAMBOO"),RoomFeature.get("SMOOTH"), RoomFeature.get("MOSAIC"), RoomFeature.get("GREEN")),
                new Room.Size(0, 0 , 0),
                98
        );
        add(
                "EAST_PIPES_HALLWAY",
                List.of(RoomFeature.get("BASE_BRICKS"), RoomFeature.get("BASE_STONE"), RoomFeature.get("HARD_FLOOR"), RoomFeature.get("HARD_CEILING"), RoomFeature.get("LARGE_BRICKS"), RoomFeature.get("EAST_PIPES"), RoomFeature.get("EAST_CHAINS")),
                List.of(),
                new Room.Size(-2, 3 , 25),
                1
        );
        add(
                "SULFUR_ROOM",
                List.of(RoomFeature.get("YELLOW_WALLS"), RoomFeature.get("YELLOW_CEILING")),
                List.of(),
                new Room.Size(0, 0 , 0),
                1
        );
    }

    /*
    public List<RoomFeature> getFeatures() { return features; }
    public List<randomFeature> getAlternates() { return alternates; }
    public Room.Size getSizeShift() { return sizeShift; }
    public int getChance() { return chance; }*/


}
