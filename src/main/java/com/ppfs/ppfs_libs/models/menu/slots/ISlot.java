
package com.ppfs.ppfs_libs.models.menu.slots;

import org.bukkit.inventory.ItemStack;

public interface ISlot {
    void setItem(ItemStack item);
    ItemStack getItem();
}
