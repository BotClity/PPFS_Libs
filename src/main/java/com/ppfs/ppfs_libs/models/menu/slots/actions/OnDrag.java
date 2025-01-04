// PPFS_Libs Plugin 
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs.models.menu.slots.actions;

import org.bukkit.event.inventory.InventoryDragEvent;

public interface OnDrag {
    boolean run (InventoryDragEvent event);
}
