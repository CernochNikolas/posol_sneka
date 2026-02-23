package cz.PosolSneka.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Herní svět složený z místností a ukazatele na aktuální hráčovu pozici.
 */
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

    public Collection<Room> getRooms() {
        return rooms.values();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }
        currentRoom = room;
    }

    public void setCurrentRoom(Room room) {
        if (room == null) throw new IllegalArgumentException("Room is null");
        currentRoom = room;
    }

    public void setStartRoom(String startRoomId) {
        setCurrentRoom(startRoomId);
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
