package cz.PosolSneka.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Reprezentuje jednu lokaci ve hře.
 * Uchovává propojení na další místnosti, volitelné požadavky na vstup
 * (např. klíč) a seznam předmětů v lokaci.
 */
public class Room {
    private final String id;
    private final String name;
    private final Map<Direction, Room> exits = new EnumMap<>(Direction.class);
    private final Map<Direction, String> exitLocks = new EnumMap<>(Direction.class);
    private final List<String> items = new ArrayList<>();

    public Room(String id, String name) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Room id is empty");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Room name is empty");
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public Map<Direction, Room> getExits() { return Collections.unmodifiableMap(exits); }

    public void setExit(Direction dir, Room target) {
        setExit(dir, target, null);
    }

    public void setExit(Direction dir, Room target, String requiredTag) {
        if (dir == null) throw new IllegalArgumentException("Direction is null");
        if (target == null) throw new IllegalArgumentException("Target room is null");
        exits.put(dir, target);

        if (requiredTag == null || requiredTag.isBlank()) {
            exitLocks.remove(dir);
        } else {
            exitLocks.put(dir, requiredTag.trim().toLowerCase());
        }
    }

    public String getExitLock(Direction dir) {
        return exitLocks.get(dir);
    }

    public Room getExit(Direction dir) {
        return exits.get(dir);
    }

    /**
     * Pohyb na sousední místnost. Pokud směr neexistuje, vrací aktuální místnost.
     */
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
                String lock = exitLocks.get(e.getKey());
                if (lock != null) sb.append(" [lock=").append(lock).append(']');
            }
        }
        return sb.toString();
    }

    public List<String> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(String itemId) {
        if (itemId == null || itemId.isBlank()) return;
        items.add(itemId.trim().toLowerCase());
    }

    public boolean removeItem(String itemId) {
        if (itemId == null) return false;
        return items.remove(itemId.trim().toLowerCase());
    }

    public boolean hasItem(String itemId) {
        if (itemId == null) return false;
        return items.contains(itemId.trim().toLowerCase());
    }
}
