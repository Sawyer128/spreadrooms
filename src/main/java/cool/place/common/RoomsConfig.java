package cool.place.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cool.place.Spreadrooms;

import java.io.*;

public class RoomsConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG = new File("config/spreadrooms.json");

    public int areaFromCenter = 100;
    public int timeToSpread = 100;
    public int timeToSpreadVariation = 20;
    public boolean timedSpread = false;

    public int maxGenRooms = 10;

    public Room.Size defaultRoomSize = new Room.Size(6, 4, 8);

    private static RoomsConfig INSTANCE;

    public static RoomsConfig get() {

        if (INSTANCE == null) { INSTANCE = load(); }

        return INSTANCE;
    }


    public static RoomsConfig load() {

        if (!CONFIG.exists()) {

            RoomsConfig roomsConfig = new RoomsConfig();
            roomsConfig.save();
            return roomsConfig;

        }

        try (FileReader reader = new FileReader(CONFIG)) {

            RoomsConfig roomsConfig = GSON.fromJson(reader, RoomsConfig.class);

            if (roomsConfig == null) { return new RoomsConfig(); }

            return roomsConfig;

        } catch (IOException e) { Spreadrooms.LOGGER.error("Could not load config file!", e); return new RoomsConfig(); }



    }

    public void save() {

        try {

            File folder = CONFIG.getParentFile();
            if (!folder.exists()) { folder.mkdirs(); }
            try (FileWriter writer = new FileWriter(CONFIG)) { GSON.toJson(this, writer); }

        } catch (IOException e) { Spreadrooms.LOGGER.error("Could not save config file!", e); }

    }



}
