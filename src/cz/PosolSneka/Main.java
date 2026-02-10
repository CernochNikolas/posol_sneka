package cz.PosolSneka;

import cz.PosolSneka.actors.Player;
import cz.PosolSneka.core.GameEngine;
import cz.PosolSneka.world.Direction;
import cz.PosolSneka.world.Room;
import cz.PosolSneka.world.World;
import cz.PosolSneka.world.WorldLoader;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        // 1) načti svět z JSON
        World world = WorldLoader.load();
        Player player = new Player();

        // 2) vytvoř engine (předpokládám konstruktor GameEngine(World))
        GameEngine engine = new GameEngine(world, player);

        // 3) testovací itemy (aby šlo hned zkoušet inventory/take)
        seedTestItems(engine.getWorld());

        System.out.println("=== Posol Sneka - TEST ===");
        System.out.println("Napis: north/south/east/west/up/down | go <dir> | look | inventory | take <item> | help | exit\n");

        Scanner sc = new Scanner(System.in);

        while (true) {
            render(engine);

            System.out.print("> ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Bye.");
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }

            if (input.equalsIgnoreCase("look")) {
                // jen znovu vykreslí stav, takže stačí continue
                continue;
            }

            if (input.equalsIgnoreCase("inventory") || input.equalsIgnoreCase("inv")) {
                var inv = engine.getPlayer().getInventory();
                if (inv.isEmpty()) System.out.println("Inventar je prazdny.");
                else System.out.println("Inventar: " + inv.list());
                continue;
            }

            if (input.toLowerCase().startsWith("take ")) {
                String itemId = input.substring(5).trim();
                if (itemId.isEmpty()) {
                    System.out.println("Pouziti: take <item>");
                    continue;
                }

                Room r = engine.getWorld().getCurrentRoom();

                // POZOR: tohle vyžaduje, aby Room měl metody getItems/removeItem/addItem
                if (r.removeItem(itemId)) {
                    engine.getPlayer().getInventory().add(itemId);
                    System.out.println("Sebral jsi: " + itemId);
                } else {
                    System.out.println("Tady to neni: " + itemId);
                }
                continue;
            }

            // Pohyb: "north" nebo "go north" nebo "jdi sever"
            Direction dir = parseDirectionFromInput(input);
            if (dir != null) {
                engine.movePlayer(dir);
                continue;
            }

            System.out.println("Neznamy prikaz. Napis 'help'.");
        }
    }

    private static void render(GameEngine engine) {
        Room r = engine.getWorld().getCurrentRoom();

        System.out.println("\n---");
        System.out.println("Jsi v: " + r.getName() + " [" + r.getId() + "]");

        // Exits
        if (r.getExits().isEmpty()) {
            System.out.println("Vychody: (zadne)");
        } else {
            System.out.println("Vychody: " + r.getExits().keySet());
        }

        // Items v místnosti (pokud máš implementované Room.getItems())
        try {
            var items = r.getItems();
            if (items.isEmpty()) System.out.println("Veci: (zadne)");
            else System.out.println("Veci: " + items);
        } catch (Exception ignored) {
            // pokud ještě nemáš itemy v Room, nic se neděje
        }
    }

    private static void printHelp() {
        System.out.println("""
                Prikazy:
                  north/south/east/west/up/down
                  go <dir>
                  look
                  inventory (inv)
                  take <item>
                  help
                  exit
                """);
    }

    private static Direction parseDirectionFromInput(String input) {
        String s = input.trim().toLowerCase();
        String token = s;

        // "go north" / "move east" / "jdi sever"
        String[] parts = s.split("\\s+");
        if (parts.length >= 2 && (parts[0].equals("go") || parts[0].equals("move") || parts[0].equals("jdi"))) {
            token = parts[1];
        }

        switch (token) {
            case "n":
            case "north":
            case "sever":
                return Direction.NORTH;

            case "s":
            case "south":
            case "jih":
                return Direction.SOUTH;

            case "e":
            case "east":
            case "vychod":
                return Direction.EAST;

            case "w":
            case "west":
            case "zapad":
                return Direction.WEST;

            case "u":
            case "up":
            case "nahoru":
                return Direction.UP;

            case "d":
            case "down":
            case "dolu":
                return Direction.DOWN;

            default:
                return null;
        }
    }

    private static void seedTestItems(World world) {
        // Tyhle řádky fungují jen když máš World.getRoom(id) + Room.addItem(...)
        try {
            Room cell3 = world.getRoom("cell3");
            if (cell3 != null) cell3.addItem("coin");

            Room classroom = world.getRoom("classroom");
            if (classroom != null) classroom.addItem("notebook_half1");

            Room keyroom = world.getRoom("keyroom");
            if (keyroom != null) keyroom.addItem("key");
        } catch (Exception ignored) {
            // Pokud zatím nemáš getRoom/addItem, tak to jen přeskočí
        }
    }
}
