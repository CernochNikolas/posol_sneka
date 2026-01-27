package cz.PosolSneka.world;

import java.util.EnumMap;
import java.util.Map;

public class Room {
    private final String id;
    private final String name;
    private final Map<Direction, Room> exits = new EnumMap<>(Direction.class);

    public Room(String id, String name) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Room id is empty");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Room name is empty");
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public Map<Direction, Room> getExits() { return exits; }

    public void setExit(Direction dir, Room target) {
        if (dir == null) throw new IllegalArgumentException("Direction is null");
        if (target == null) throw new IllegalArgumentException("Target room is null");
        exits.put(dir, target);
    }


    public Room move(Direction dir) {
        return exits.getOrDefault(dir, this);
    }
}
