package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

/**
 * Původně překlep názvu (Wit), nechávám kvůli kompatibilitě se souborem.
 * Chová se jako příkaz WAIT/ČEKEJ.
 */
public class WitCommand implements Command {
    @Override
    public void execute(GameEngine engine) {
        engine.waitTurn();
    }
}
