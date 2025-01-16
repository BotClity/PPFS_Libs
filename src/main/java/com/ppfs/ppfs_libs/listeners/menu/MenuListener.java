// PPFS_Libs Plugin 
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs.listeners.menu;

import com.ppfs.ppfs_libs.models.menu.Menu;
import com.ppfs.ppfs_libs.models.menu.slots.Slot;
import com.ppfs.ppfs_libs.service.MenuService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;

public class MenuListener implements Listener {


    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu) {
            event.setCancelled(menu.getInventoryDrag().run(event));
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu) {
            if (event.getClickedInventory() == null) {
                menu.getOutsideClick().run(event);
                return;
            }else if (event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
                menu.getOwnInventoryClick().run(event);
                return;
            }
            menu.getInventoryClick().run(event);
            Slot slot = menu.getSlot(event.getSlot());

            if (slot!=null)
                slot.getListener().run(event);

            boolean interact = menu.isTakeItems();
            if (!interact)
                event.setCancelled(true);
        }

    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu) {
            menu.getInventoryClose().run(event);
            if (event.getReason() == InventoryCloseEvent.Reason.PLAYER &&  !menu.isCanBeClosed()){
                menu.open(event.getPlayer());
                return;
            }
            menu.getMenuService().onInventoryClose(event);
        }
    }

    @EventHandler
    private void onInteractItem(InventoryInteractEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu) {
            event.setCancelled(menu.getInventoryInteract().run(event));

            boolean interact = menu.isTakeItems();
            if (!interact)
                event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getInventory().getHolder() instanceof Menu menu) {
            event.setCancelled(menu.getInventoryDrop().run(event));
        }

    }

}
