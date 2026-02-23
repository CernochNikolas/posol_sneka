package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public class PourSaltCommand implements Command {
    @Override
    public void execute(GameEngine engine) {
        engine.pourSalt();
    }
}
