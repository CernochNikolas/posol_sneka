package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public class HelpCommand implements Command {
    @Override
    public void execute(GameEngine engine) {
        engine.printHelp();
    }
}
