package cz.PosolSneka.world;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Room {
    private final String id;
    private final String name;
    private final Map<Direction, Room> exits = new EnumMap<>(Direction.class);

    private final List<String> items = new ArrayList<>();

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" (").append(id).append(")\n");
        sb.append("Exits: ");

        if (exits.isEmpty()) {
            sb.append("(none)");
        } else {
            boolean first = true;
            for (var e : exits.entrySet()) {
                if (!first) sb.append(", ");
                first = false;
                sb.append(e.getKey()).append(" -> ").append(e.getValue().getId());
            }
        }
        return sb.toString();
    }

    public List<String> getItems() { return items; }

    public void addItem(String itemId) { items.add(itemId); }

    public boolean removeItem(String itemId) { return items.remove(itemId); }

}
