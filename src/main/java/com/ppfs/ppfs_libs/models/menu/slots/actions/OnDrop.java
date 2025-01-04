// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs.models.menu.slots.actions;

import org.bukkit.event.player.PlayerDropItemEvent;

public interface OnDrop {
    boolean run(PlayerDropItemEvent action);
}
