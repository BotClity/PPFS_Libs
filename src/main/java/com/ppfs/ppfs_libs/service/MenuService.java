// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs.service;

import com.ppfs.ppfs_libs.models.menu.Menu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuService implements Serializable, Listener {
    private final List<Menu> activeMenus;
    @Getter
    private final JavaPlugin plugin;

    public MenuService(JavaPlugin plugin) {
        this.plugin = plugin;
        activeMenus = new ArrayList<>();
    }


    public void addActiveMenu(Menu menu) {
        if (activeMenus.contains(menu)) return;
        activeMenus.add(menu);
    }

    public void removeActiveMenu(Menu menu) {
        activeMenus.remove(menu);
    }

    public boolean hasActiveMenu(Menu menu) {
        return activeMenus.contains(menu);
    }

    public void openMenu(Menu menu, Player player) {
        menu.setMenuService(this);
        addActiveMenu(menu);
        menu.open(player);
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        Bukkit.getScheduler().runTask(plugin, ()->{
            if (event.getInventory().getViewers().isEmpty())removeActiveMenu((Menu) event.getInventory().getHolder());
        });
    }

}