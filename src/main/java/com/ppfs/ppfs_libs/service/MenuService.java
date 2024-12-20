package com.ppfs.ppfs_libs.service;

import com.ppfs.ppfs_libs.PPFS_Libs;
import com.ppfs.ppfs_libs.listeners.menu.MenuOnClick;
import com.ppfs.ppfs_libs.listeners.menu.MenuOnClose;
import com.ppfs.ppfs_libs.listeners.menu.MenuOnDrop;
import com.ppfs.ppfs_libs.listeners.menu.slots.SlotListener;
import com.ppfs.ppfs_libs.models.menu.Menu;
import com.ppfs.ppfs_libs.models.menu.slots.Slot;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuService implements Serializable, Listener {
    private static MenuService instance;
    private final List<Menu> menus;
    private final PPFS_Libs plugin;

    public MenuService() {
        menus = new ArrayList<>();
        plugin = PPFS_Libs.getInstance();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private static void load() {
        instance = new MenuService();
    }


    public static MenuService getInstance() {
        if (instance == null) load();
        return instance;
    }

    public void registerMenu(Menu menu) {
        menus.add(menu);
    }

    public void unregisterMenu(Menu menu) {
        menus.remove(menu);
    }

    public void unregisterAllmenus() {
        menus.clear();
    }

    public void unregisterMenu(String id) {
        menus.removeIf(menu -> menu.getId().equals(id));
    }

    public void unregisterMenu(Inventory inventory) {
        menus.removeIf(menu -> menu.getInventory().equals(inventory));
    }

    public Menu getMenu(String id) {
        return menus.stream().filter(menu -> menu.getId().equals(id)).findFirst().orElse(null);
    }

    public Menu getMenu(HumanEntity player) {
        return menus.stream().filter(m -> m.getViewers().containsKey(player.getName())).findFirst().orElse(null);
    }

    public Menu getMenu(Inventory inventory) {
        return menus.stream().filter(menu -> menu.getInventory().equals(inventory)).findFirst().orElse(null);
    }


    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Menu menu = getMenu(event.getWhoClicked());
        if (menu == null) {
            return;
        }
        if (menu.hasOnClick()) {
            boolean result = menu.getClick().run(
                    new MenuOnClick.Action(
                            event.getView(),
                            event.getWhoClicked(),
                            event.getClickedInventory(),
                            event.getSlot(),
                            event.getHotbarButton()
                    )
            );
            event.setCancelled(!result);
        }
        if (menu.isInteractDisabled()) event.setCancelled(true);


        Player player = (Player) event.getWhoClicked();
        Slot slot = menu.getSlot(event.getSlot());

        if (slot == null) {
            return;
        }

//        if (slot.isInteractDisabled()) {
//            event.setCancelled(true);
//        }

        if (!slot.hasListener()) {
            return;
        }

        SlotListener listener = slot.getListener();
        ClickType clickType = event.getClick();

        if (clickType == ClickType.LEFT) {
            listener.onLeftClick(player);
        } else if (clickType == ClickType.RIGHT) {
            listener.onRightClick(player);
        } else if (clickType == ClickType.SHIFT_LEFT) {
            listener.onShiftLeftClick(player);
        } else if (clickType == ClickType.SHIFT_RIGHT) {
            listener.onShiftRightClick(player);
        } else if (clickType == ClickType.MIDDLE) {
            listener.onMiddleClick(player);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Menu menu = menus.stream().filter(m -> m.getViewers().containsKey(event.getPlayer().getName())).findFirst().orElse(null);
        if (menu == null) return;
        if (!menu.hasOnClose()) return;
        boolean result = menu.getOnClose().run(new MenuOnClose.Action(event.getPlayer(), inventory));
        if (!result)
            menu.open(event.getPlayer());
        else {
            unregisterMenu(menu);
        }
    }

    @EventHandler
    private void onInteractItem(InventoryInteractEvent event) {
        Inventory inventory = event.getInventory();
        Menu menu = getMenu(event.getWhoClicked());
        if (menu == null) return;
        if (menu.isInteractDisabled()) event.setCancelled(true);

    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        Inventory inventory = event.getPlayer().getInventory();
        Menu menu = getMenu(event.getPlayer());
        if (menu == null) return;
        if (!menu.hasOnDrop()) return;
        boolean result = menu.getOnDrop().run(new MenuOnDrop.Action(
                event.getItemDrop(),
                event.getPlayer()
        ));
        event.setCancelled(!result);

    }


}