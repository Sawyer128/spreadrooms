package cool.place.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomFeatureSetStupid {

    private static final Map<String, featureSetData> featureSets = new HashMap<>();

    public record randomFeature(String featureName, int featureChance) { }

    private record featureSetData(List<RoomFeaturStupid> features, List<randomFeature> alternates, Room.Size sizeShift, int chance) {}

    public static List<RoomFeaturStupid> getFeatures(String name) { return featureSets.get(name).features; }
    public static List<randomFeature> getAlternates(String name) { return featureSets.get(name).alternates; }
    public static Room.Size getSizeShift(String name) { return featureSets.get(name).sizeShift; }
    public static int getChance(String name) { return featureSets.get(name).chance; }



}
