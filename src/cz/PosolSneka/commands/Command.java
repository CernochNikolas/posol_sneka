package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

/**
 * Základ příkazu v command patternu.
 */
@FunctionalInterface
public interface Command {
    void execute(GameEngine engine);
}
