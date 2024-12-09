package com.ppfs.ppfs_libs.listeners.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public interface MenuOnDrop {
    boolean run(Action action);

    @Getter
    @AllArgsConstructor
    class Action {
        private final Item item;
        private final Player player;
    }
}
