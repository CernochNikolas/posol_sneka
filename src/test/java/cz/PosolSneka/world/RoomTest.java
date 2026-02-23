package cz.PosolSneka.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void setExitConnectsRooms() {
        Room roomA = new Room("a", "Mistnost A");
        Room roomB = new Room("b", "Mistnost B");

        roomA.setExit(Direction.NORTH, roomB);

        assertSame(roomB, roomA.getExit(Direction.NORTH));
    }

    @Test
    void setExitWithLockStoresLockTag() {
        Room roomA = new Room("a", "Mistnost A");
        Room roomB = new Room("b", "Mistnost B");

        roomA.setExit(Direction.EAST, roomB, "storage_key");

        assertSame(roomB, roomA.getExit(Direction.EAST));
        assertEquals("storage_key", roomA.getExitLock(Direction.EAST));
    }

    @Test
    void addAndRemoveItemWorks() {
        Room room = new Room("cell2", "Cela 2");

        assertFalse(room.hasItem("bone"));

        room.addItem("bone");
        assertTrue(room.hasItem("bone"));

        boolean removed = room.removeItem("bone");
        assertTrue(removed);
        assertFalse(room.hasItem("bone"));
    }

    @Test
    void removeNonExistingItemReturnsFalse() {
        Room room = new Room("cell3", "Cela 3");

        boolean removed = room.removeItem("not_existing_item");

        assertFalse(removed);
    }
}