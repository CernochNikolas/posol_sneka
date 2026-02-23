package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public class UseCommand implements Command {
    private final String itemId;
    private final String target;

    public UseCommand(String itemId, String target) {
        this.itemId = itemId;
        this.target = target;
    }

    @Override
    public void execute(GameEngine engine) {
        engine.use(itemId, target);
    }
}
