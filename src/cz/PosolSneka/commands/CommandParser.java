package cz.PosolSneka.commands;

import cz.PosolSneka.world.Direction;

import java.util.Locale;

/**
 * Převádí textový vstup uživatele na objekt Command.
 */
public class CommandParser {

    public Command parse(String input) {
        if (input == null) return null;

        String s = input.trim();
        if (s.isEmpty()) return null;

        String[] parts = s.split("\\s+");
        String first = parts[0].toLowerCase(Locale.ROOT);

        // směr samotný nebo po 'jdi/go/move'
        if (first.equals("go") || first.equals("move") || first.equals("jdi")) {
            if (parts.length < 2) return null;
            return parseDirection(parts[1]);
        }

        Command movement = parseDirection(first);
        if (movement != null) return movement;

        return switch (first) {
            case "help", "napoveda", "nápověda" -> new HelpCommand();
            case "inventory", "inventar", "inv" -> new InventoryCommand();
            case "cekej", "wait", "wit" -> new WitCommand();
            case "look", "rozhledni", "rozhledni_se" -> (engine) -> engine.render();
            case "prohledej", "search" -> new SearchCommand();
            case "mluv", "talk" -> new TalkCommand();
            case "kric", "křič", "scream" -> new ScreamCommand();
            case "nasyp" -> parsePour(parts);
            case "cti", "čti", "read" -> parseRead(parts);
            case "seber", "take", "pickup" -> parsePickup(parts);
            case "pouzij", "použij", "use" -> parseUse(parts, s);
            default -> null;
        };
    }

    private Command parsePour(String[] parts) {
        if (parts.length >= 2) {
            String what = parts[1].toLowerCase(Locale.ROOT);
            if (what.equals("sul") || what.equals("sůl") || what.equals("salt")) {
                return new PourSaltCommand();
            }
        }
        return null;
    }

    private Command parseRead(String[] parts) {
        if (parts.length >= 2) {
            return new ReadCommand(String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)));
        }
        return new ReadCommand(null);
    }

    private Command parsePickup(String[] parts) {
        if (parts.length < 2) return null;
        return new PickupCommand(String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)));
    }

    private Command parseUse(String[] parts, String original) {
        if (parts.length < 2) return null;

        // jednoduché "pouzij X" nebo "pouzij X na Y"
        String lower = original.toLowerCase(Locale.ROOT);
        int naIdx = lower.indexOf(" na ");
        if (naIdx > 0) {
            String prefix = original.substring(0, naIdx).trim();
            String suffix = original.substring(naIdx + 4).trim();
            String[] p = prefix.split("\\s+", 2);
            String item = p.length >= 2 ? p[1] : null;
            return new UseCommand(item, suffix);
        }

        int onIdx = lower.indexOf(" on ");
        if (onIdx > 0) {
            String prefix = original.substring(0, onIdx).trim();
            String suffix = original.substring(onIdx + 4).trim();
            String[] p = prefix.split("\\s+", 2);
            String item = p.length >= 2 ? p[1] : null;
            return new UseCommand(item, suffix);
        }

        return new UseCommand(String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)), null);
    }

    private Command parseDirection(String token) {
        switch (token.toLowerCase(Locale.ROOT)) {
            case "n", "north", "sever" -> {
                return new MoveCommand(Direction.NORTH);
            }
            case "s", "south", "jih" -> {
                return new MoveCommand(Direction.SOUTH);
            }
            case "e", "east", "vychod", "východ" -> {
                return new MoveCommand(Direction.EAST);
            }
            case "w", "west", "zapad", "západ" -> {
                return new MoveCommand(Direction.WEST);
            }
            case "u", "up", "nahoru" -> {
                return new MoveCommand(Direction.UP);
            }
            case "d", "down", "dolu", "dolů" -> {
                return new MoveCommand(Direction.DOWN);
            }
            default -> {
                return null;
            }
        }
    }
}
