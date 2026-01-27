package cz.PosolSneka.world;

public enum Direction {
    NORTH, SOUTH, EAST, WEST, UP, DOWN;

    public static Direction fromString(String s) {
        return Direction.valueOf(s.trim().toUpperCase());
    }
}
