package cz.PosolSneka;

import cz.PosolSneka.actors.Player;
import cz.PosolSneka.commands.Command;
import cz.PosolSneka.commands.CommandParser;
import cz.PosolSneka.core.GameEngine;
import cz.PosolSneka.world.World;
import cz.PosolSneka.world.WorldLoader;

import java.util.Scanner;

/**
 * Spouštěcí třída textové verze hry Posol šneka.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        World world = WorldLoader.load();
        Player player = new Player();
        GameEngine engine = new GameEngine(world, player);
        CommandParser parser = new CommandParser();

        engine.printWelcome();

        try (Scanner sc = new Scanner(System.in)) {
            while (!engine.isGameWon()) {
                engine.render();
                System.out.print("> ");

                if (!sc.hasNextLine()) break;
                String input = sc.nextLine().trim();
                if (input.isEmpty()) continue;

                String lower = input.toLowerCase();
                if (lower.equals("exit") || lower.equals("konec") || lower.equals("quit")) {
                    System.out.println("Konec hry.");
                    break;
                }

                Command command = parser.parse(input);
                if (command == null) {
                    System.out.println("Neznamy prikaz. Zkus 'napoveda'.");
                    continue;
                }

                try {
                    command.execute(engine);
                } catch (Exception e) {
                    // obrana proti shozeni hry spatnym vstupem / chybou logiky
                    System.out.println("Doslo k chybe pri zpracovani prikazu: " + e.getMessage());
                }
            }
        }
    }
}
