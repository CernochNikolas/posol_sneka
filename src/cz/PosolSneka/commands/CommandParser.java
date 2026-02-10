package cz.PosolSneka.commands;

import cz.PosolSneka.world.Direction;

public class CommandParser {

    public Command parse(String input) {
        if (input == null) return null;

        String s = input.trim().toLowerCase();
        if (s.isEmpty()) return null;

        String[] parts = s.split("\\s+");
        String first = parts[0];

        // "go north" / "move east" / "jdi sever"
        if (first.equals("go") || first.equals("move") || first.equals("jdi")) {
            if (parts.length < 2) return null;
            return parseDirection(parts[1]);
        }

        // "north" / "n" / "sever"
        return parseDirection(first);
    }

    private Command parseDirection(String token) {
        switch (token) {
            case "n":
            case "north":
            case "sever":
                return new MoveCommand(Direction.NORTH);

            case "s":
            case "south":
            case "jih":
                return new MoveCommand(Direction.SOUTH);

            case "e":
            case "east":
            case "vychod":
                return new MoveCommand(Direction.EAST);

            case "w":
            case "west":
            case "zapad":
                return new MoveCommand(Direction.WEST);

            case "u":
            case "up":
            case "nahoru":
                return new MoveCommand(Direction.UP);

            case "d":
            case "down":
            case "dolu":
                return new MoveCommand(Direction.DOWN);

            default:
                return null;
        }
    }
}

