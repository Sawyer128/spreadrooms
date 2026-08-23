package cool.place.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cool.place.Spreadrooms;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoomsConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG = new File("config/spreadrooms.json");

    public int areaFromCenter = 100;
    public int timeToSpread = 20;
    public int timeToSpreadVariation = 0;
    public boolean timedSpread = true;

    public boolean strictGeneration = true;
    public boolean generateOnPlane = true;
    public boolean connectRooms = true;
    public int maxGenRooms = 5;
    public Room.Size defaultRoomSize = new Room.Size(6, 4, 6);

    public Room.Size sizeVariation = new Room.Size(7, 1, 7);
    public Room.Size sizeVariationShift = new Room.Size(1, 0, 1);
    public Room.Size sizeVariationFactor = new Room.Size(2, 2, 2);

    public boolean roomBlocking = true;
    public Room.Size maxRoomSize = new Room.Size(8, 6, 8);

    public Map<RoomFeatureEnum, String> featureStates = new LinkedHashMap<>();

    private static RoomsConfig INSTANCE;

    public static RoomsConfig get() {

        if (INSTANCE == null) { INSTANCE = load(); }

        return INSTANCE;

    }

    public static RoomsConfig load() {

        RoomsConfig roomsConfig;

        if (!CONFIG.exists()) {

            roomsConfig = new RoomsConfig();

        }
        else {
            try (FileReader reader = new FileReader(CONFIG)) {

                roomsConfig = GSON.fromJson(reader, RoomsConfig.class);

                if (roomsConfig == null) { roomsConfig = new RoomsConfig(); }

            } catch (IOException e) { Spreadrooms.LOGGER.error("Could not load config file!", e); roomsConfig = new RoomsConfig(); }
        }

        for (RoomFeatureEnum feature : RoomFeatureEnum.values()) {
            roomsConfig.featureStates.putIfAbsent(feature, BuiltInRegistries.BLOCK.getKey(feature.getDefaultState().getBlock()).toString());
        }

        roomsConfig.save();

        return roomsConfig;

    }

    public void save() {

        try {

            File folder = CONFIG.getParentFile();
            if (!folder.exists()) { folder.mkdirs(); }
            try (FileWriter writer = new FileWriter(CONFIG)) { GSON.toJson(this, writer); }

        } catch (IOException e) { Spreadrooms.LOGGER.error("Could not save config file!", e); }

    }

    public static void reload() {
        INSTANCE = load();
    }

}
