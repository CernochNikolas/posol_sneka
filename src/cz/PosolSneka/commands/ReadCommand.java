package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public class ReadCommand implements Command {
    private final String itemId;

    public ReadCommand(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public void execute(GameEngine engine) {
        engine.read(itemId);
    }
}
