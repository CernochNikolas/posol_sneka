package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public interface Command {
    void execute(GameEngine engine);
}

