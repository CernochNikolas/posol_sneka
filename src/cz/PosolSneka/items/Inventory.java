package cz.PosolSneka.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private final List<String> items = new ArrayList<>();

    public void add(String itemId) {
        items.add(itemId);
    }

    public boolean remove(String itemId) {
        return items.remove(itemId);
    }

    public boolean has(String itemId) {
        return items.contains(itemId);
    }

    public void clear() {
        items.clear();
    }

    public List<String> list() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
