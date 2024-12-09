
package com.ppfs.ppfs_libs.models.menu;

import org.bukkit.entity.Player;

public interface IMenu {
    void open(Player player);
    void close(Player player);
    String getTitle();
}
