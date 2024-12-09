package com.ppfs.ppfs_libs.listeners.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public interface MenuOnClose {
    boolean run(Action action);

    @Getter
    @AllArgsConstructor
    class Action {
        private final HumanEntity player;
        private final Inventory inventory;
    }
}
