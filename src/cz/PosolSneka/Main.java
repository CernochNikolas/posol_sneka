package cz.PosolSneka;

import cz.PosolSneka.world.Direction;
import cz.PosolSneka.world.World;
import cz.PosolSneka.world.WorldLoader;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        World world = WorldLoader.load();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Jsi v: " + world.getCurrentRoom().getName());
            System.out.print("Smer (NORTH/SOUTH/EAST/WEST/UP/DOWN) nebo EXIT: ");
            String in = sc.nextLine().trim();
            if (in.equalsIgnoreCase("EXIT")) break;

            try {
                Direction dir = Direction.fromString(in);
                world.move(dir);
            } catch (Exception e) {
                System.out.println("Neplatny smer.");
            }
        }
    }
}
