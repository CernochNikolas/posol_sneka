package cz.PosolSneka.world;

import java.util.List;
import java.util.Map;

public class WorldData {
    public String start_room;
    public List<RoomData> rooms;
}

class RoomData {
    public String id;
    public String name;
    public Map<String, String> exits;
}
