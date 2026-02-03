package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;
import cz.PosolSneka.world.Direction;
import cz.PosolSneka.world.Room;

import java.util.ArrayList;

public class MoveCommand implements Command {
    private final Direction dir;
    public MoveCommand(Direction dir) { this.dir = dir; }

    @Override
    public void execute(GameEngine engine) {
        engine.movePlayer(dir);
    }
}

