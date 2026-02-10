package cz.PosolSneka.commands;

import cz.PosolSneka.core.GameEngine;

public class PickupCommand implements Command {
    private final String itemId;

    public PickupCommand(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public void execute(GameEngine engine) {
        var room = engine.getWorld().getCurrentRoom();

        if (room.removeItem(itemId)) {
            engine.getPlayer().getInventory().add(itemId);
            System.out.println("Sebral jsi: " + itemId);
        } else {
            System.out.println("Tady to neni: " + itemId);
        }
    }
}
