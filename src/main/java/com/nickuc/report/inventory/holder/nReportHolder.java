package com.nickuc.report.inventory.holder;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class nReportHolder implements InventoryHolder {

    private Inventory inventory;

    public void init(Inventory inventory) {
        if (this.inventory != null) {
            throw new IllegalStateException("Inventory already set!");
        }

        this.inventory = inventory;
    }

    public Inventory getInventory() {
        if (inventory == null) {
            throw new IllegalStateException("Inventory not defined yet!");
        }

        return inventory;
    }
}
