package cz.PosolSneka.world;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WorldLoader {

    public static World load() throws Exception {
        InputStream is = WorldLoader.class.getResourceAsStream("/world.json");
        if (is == null) {
            throw new IllegalArgumentException("world.json not found. Put it in src/main/resources/world.json");
        }

        ObjectMapper mapper = new ObjectMapper();
        WorldData data = mapper.readValue(is, WorldData.class);

        World world = new World();
        Map<String, Room> created = new HashMap<>();

        // 1) vytvoř místnosti
        for (RoomData rd : data.rooms) {
            Room r = new Room(rd.id, rd.name);
            created.put(rd.id, r);
            world.addRoom(r);
        }

        // 2) propojit exits
        for (RoomData rd : data.rooms) {
            Room from = created.get(rd.id);
            if (from == null) continue;
            if (rd.exits == null) continue;

            for (var e : rd.exits.entrySet()) {
                Direction dir = Direction.valueOf(e.getKey().trim().toUpperCase());

                Room to = created.get(e.getValue());
                if (to == null) {
                    throw new IllegalArgumentException("Unknown room id in exits: " + e.getValue());
                }
                from.setExit(dir, to);
            }
        }

        // 3) start místnost
        world.setStartRoom(data.start_room);
        return world;
    }
}
