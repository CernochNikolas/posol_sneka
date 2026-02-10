package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public class InventoryCommand implements Command {
    @Override
    public void execute(GameEngine engine) {
        var inv = engine.getPlayer().getInventory();
        if (inv.isEmpty()) {
            System.out.println("Inventar je prazdny.");
        } else {
            System.out.println("Inventar: " + inv.list());
        }
    }
}
