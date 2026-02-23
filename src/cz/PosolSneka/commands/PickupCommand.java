package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public class PickupCommand implements Command {
    private final String itemId;

    public PickupCommand(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public void execute(GameEngine engine) {
        engine.pickup(itemId);
    }
}
