package cz.PosolSneka.world;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final Map<String, Room> rooms = new HashMap<>();
    private Room currentRoom;

    public void addRoom(Room room) {
        if (room == null) throw new IllegalArgumentException("Room is null");
        rooms.put(room.getId(), room);
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setStartRoom(String startRoomId) {
        Room start = rooms.get(startRoomId);
        if (start == null) {
            throw new IllegalArgumentException("Start room not found: " + startRoomId);
        }
        currentRoom = start;
    }

    /**
     * Pohyb dle zadání:
     * - když exit neexistuje, zůstáváš
     */
    public void move(Direction dir) {
        if (currentRoom == null) {
            throw new IllegalStateException("Current room is null. Did you call setStartRoom()?");
        }
        currentRoom = currentRoom.move(dir);
    }
}
