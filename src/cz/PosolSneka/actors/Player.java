package cz.PosolSneka.actors;

import cz.PosolSneka.items.Inventory;

public class Player {
    private final Inventory inventory = new Inventory();

    public Inventory getInventory() {
        return inventory;
    }

    public void clearInventory() {
        inventory.clear();
    }
}
