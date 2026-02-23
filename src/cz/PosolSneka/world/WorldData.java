package cz.PosolSneka.world;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * DTO struktury pro načítání světa z JSON pomocí Jacksonu.
 */
public class WorldData {
    public String start_room;
    public List<RoomData> rooms;
}

class RoomData {
    public String id;
    public String name;
    public Map<String, ExitData> exits;
    public JsonNode items; // může být string, pole nebo null
}

class ExitData {
    public String to;
    public String locked;
}
