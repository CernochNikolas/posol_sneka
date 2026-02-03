package cz.PosolSneka.core;

import cz.PosolSneka.world.Direction;
import cz.PosolSneka.world.World;
import cz.PosolSneka.world.Room;

public class GameEngine {

    private final World world;

    public GameEngine(World world) {
        this.world = world;
    }

    public void movePlayer(Direction dir) {
        Room before = world.getCurrentRoom();
        System.out.println(before);
        world.move(dir);
        Room after = world.getCurrentRoom();

        if (after == before) {
            System.out.println("Tim smerem to nejde.");
        } else {
            System.out.println("Presel jsi do: " + after.getName());
            // později: after.onEnter(this);
        }
    }

    public World getWorld() {
        return world;
    }
}
