package cz.PosolSneka.world;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Načte herní svět ze souboru world.json (resources) a propojí místnosti.
 */
public class WorldLoader {

    public static World load() throws Exception {
        try (InputStream is = WorldLoader.class.getResourceAsStream("/world.json")) {
            if (is == null) {
                throw new IllegalArgumentException("world.json not found. Put it in resources/world.json");
            }
            return load(is);
        }
    }

    public static World load(InputStream is) throws Exception {
        if (is == null) throw new IllegalArgumentException("InputStream is null");

        ObjectMapper mapper = new ObjectMapper();
        WorldData data = mapper.readValue(is, WorldData.class);

        if (data.rooms == null || data.rooms.isEmpty()) {
            throw new IllegalArgumentException("World must contain at least one room");
        }

        World world = new World();
        Map<String, Room> created = new HashMap<>();

        // 1) vytvoř místnosti
        for (RoomData rd : data.rooms) {
            Room r = new Room(rd.id, rd.name);
            created.put(rd.id, r);
            world.addRoom(r);

            // načtení itemů (string nebo pole)
            appendItemsFromJson(r, rd.items);
        }

        // 2) propojit exits + lock pravidla
        for (RoomData rd : data.rooms) {
            Room from = created.get(rd.id);
            if (from == null || rd.exits == null) continue;

            for (var e : rd.exits.entrySet()) {
                Direction dir;
                try {
                    dir = Direction.valueOf(e.getKey().trim().toUpperCase());
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Unknown direction in room '" + rd.id + "': " + e.getKey());
                }

                ExitData exitData = e.getValue();
                if (exitData == null || exitData.to == null || exitData.to.isBlank()) {
                    throw new IllegalArgumentException("Missing 'to' in exit " + dir + " of room " + rd.id);
                }

                Room to = created.get(exitData.to);
                if (to == null) {
                    throw new IllegalArgumentException("Unknown room id in exits: " + exitData.to);
                }
                from.setExit(dir, to, exitData.locked);
            }
        }

        // 3) start místnost
        world.setStartRoom(data.start_room);
        return world;
    }

    private static void appendItemsFromJson(Room room, JsonNode itemsNode) {
        if (itemsNode == null || itemsNode.isNull()) return;

        if (itemsNode.isTextual()) {
            room.addItem(itemsNode.asText());
            return;
        }

        if (itemsNode.isArray()) {
            for (JsonNode node : itemsNode) {
                if (node != null && node.isTextual()) {
                    room.addItem(node.asText());
                }
            }
        }
    }
}
