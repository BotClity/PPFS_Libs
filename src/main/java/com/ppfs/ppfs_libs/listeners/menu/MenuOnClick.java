package com.ppfs.ppfs_libs.listeners.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public interface MenuOnClick {
    boolean run(Action action);

    @Getter
    @AllArgsConstructor
    class Action {
        private final InventoryView view;
        private final HumanEntity player;
        private final Inventory clicked;
        private final int slot;
        private final int hotbar;
    }
}
