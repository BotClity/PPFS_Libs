package com.ppfs.ppfs_libs.models.menu;

import com.ppfs.ppfs_libs.models.menu.slots.Slot;
import com.ppfs.ppfs_libs.models.menu.slots.actions.*;
import com.ppfs.ppfs_libs.service.MenuService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Serializable;
import java.util.HashMap;

@Setter
@Getter
public class Menu implements Serializable, InventoryHolder {

    protected MenuService menuService;

    private final String title;
    private final String id;
    private boolean canBeClosed = true;
    private boolean takeItems = false;

    private transient Inventory inventory;

    private HashMap<Integer, Slot> slots;

    private OnClose inventoryClose;
    private OnClick outsideClick;
    private OnClick inventoryClick;
    private OnClick ownInventoryClick;
    private OnDrop inventoryDrop;
    private OnDrag inventoryDrag;
    private OnInteract inventoryInteract;

    public Menu(String id, String title, int rows, Plugin plugin) {
        this.id = id;
        this.title = title;
        this.slots = new HashMap<>();
        this.inventory = Bukkit.createInventory(this, rows, title);
    }

    public Menu clearMenu() {
        slots.clear();
        inventory.clear();
        return this;
    }


    public Slot getSlot(int slot) {
        return slots.get(slot);
    }

    public Menu addSlot(int position, Slot slot) {
        slot.setPosition(position);
        slots.put(position, slot);
        return this;
    }

    public Menu addSlot(Slot slot) {
        int pos = slot.getPosition();
        if (pos == -1) pos = inventory.firstEmpty();
        if (pos == -1) return this;
        addSlot(pos, slot);
        return this;
    }

    public void updateInventory(HumanEntity player) {
        if (menuService == null) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                inventory.clear();
                slots.forEach((position, slot) -> {
                    if (slot != null) {
                        inventory.setItem(position, slot.toItemStack(player));
                    }
                });
                inventory.getViewers().forEach(viewer -> {
                    if (!(viewer instanceof Player player)) return;
                    player.updateInventory();
                });
            }
        }.runTask(menuService.getPlugin());

    }

    public void open(HumanEntity player) {
        updateInventory(player);
        player.openInventory(inventory);
    }

    public void open(Player player) {
        updateInventory(player);
        player.openInventory(inventory);
    }

    public OnClose getInventoryClose() {
        if (!hasInventoryClose()){
            return event -> false;
        }
        return inventoryClose;
    }
    public OnClick getInventoryClick() {
        if (!hasInventoryClick()){
            return event -> false;
        }
        return inventoryClick;
    }

    public OnClick getOwnInventoryClick() {
        if (!hasOwnInventoryClick()){
            return event -> false;
        }
        return ownInventoryClick;
    }

    public OnDrop getInventoryDrop() {
        if (!hasInventoryDrop()){
            return event -> false;
        }
        return inventoryDrop;
    }


    public OnInteract getInventoryInteract() {
        if (!hasInventoryInteract()){
            return event -> false;
        }
        return inventoryInteract;
    }

    public OnClick getOutsideClick() {
        if (!hasOutsideClick()){
            return event -> false;
        }
        return outsideClick;
    }

    public void close(){
        inventory.getViewers().forEach(HumanEntity::closeInventory);
    }

    public void close(Player player) {
        if (inventory.getViewers().contains(player)) {
            player.closeInventory();
        }
    }

    public boolean hasInventoryClose() {
        return inventoryClose != null;
    }

    public boolean hasInventoryClick() {
        return inventoryClick != null;
    }

    public boolean hasOwnInventoryClick() {
        return ownInventoryClick != null;
    }

    public boolean hasInventoryDrop() {
        return inventoryDrop != null;
    }

    public boolean hasInventoryInteract(){
         return inventoryInteract != null;
    }
    public boolean hasInventoryDrag(){
        return inventoryDrag != null;
    }
    public boolean hasOutsideClick() {
        return outsideClick != null;
    }
}
