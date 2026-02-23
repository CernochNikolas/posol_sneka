package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public class TalkCommand implements Command {
    @Override
    public void execute(GameEngine engine) {
        engine.talk();
    }
}
