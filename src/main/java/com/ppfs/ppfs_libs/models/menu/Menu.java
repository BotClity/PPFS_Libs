package com.ppfs.ppfs_libs.models.menu;

import com.ppfs.ppfs_libs.PPFS_Libs;
import com.ppfs.ppfs_libs.listeners.menu.MenuOnClick;
import com.ppfs.ppfs_libs.listeners.menu.MenuOnClose;
import com.ppfs.ppfs_libs.listeners.menu.MenuOnDrop;
import com.ppfs.ppfs_libs.models.menu.slots.Slot;
import com.ppfs.ppfs_libs.service.MenuService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Serializable;
import java.util.HashMap;

@Setter
@Getter
public class Menu implements IMenu, Serializable {
    private final String title;
    private final String id;
    private transient Plugin plugin;

    private transient Inventory inventory;
    private transient HashMap<String, HumanEntity> viewers;

    private HashMap<Integer, Slot> slots;
    private boolean interactDisabled = true;
    private MenuOnClose onClose;
    private MenuOnClick click;
    private MenuOnDrop onDrop;

    public Menu(String id, String title, int rows, Plugin plugin) {
        this.plugin = plugin;
        this.id = id;
        this.title = title;
        this.slots = new HashMap<>();
        this.inventory = Bukkit.createInventory(null, rows, title);
        this.viewers = new HashMap<>();
    }

    public Menu clearMenu() {
        slots.clear();
        inventory.clear();
        return this;
    }

    public void initInventory(int rows) {
        this.inventory = Bukkit.createInventory(null, rows, title);
        this.viewers = new HashMap<>();
        updateInventory();
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

    public void updateInventory() {
        if (plugin == null) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                inventory.clear();
                slots.forEach((position, slot) -> {
                    if (slot != null) {
                        inventory.setItem(position, slot.toItemStack());
                    }
                });
                inventory.getViewers().forEach(viewer -> {
                    if (!(viewer instanceof Player player)) return;
                    player.updateInventory();
                });
            }
        }.runTask(plugin);

    }

    public void open(HumanEntity player) {
        updateInventory();
        player.openInventory(inventory);
        viewers.put(player.getName(), player);
        MenuService.getInstance().registerMenu(this);
    }

    @Override
    public void open(Player player) {
        updateInventory();
        player.openInventory(inventory);
        viewers.put(player.getName(), player);
        MenuService.getInstance().registerMenu(this);
    }

    public void close(Player player) {
        if (inventory.getViewers().contains(player)) {
            player.closeInventory();
            viewers.remove(player.getName());
        }
    }

    public boolean hasOnClose() {
        return onClose != null;
    }

    public boolean hasOnClick() {
        return click != null;
    }

    public boolean hasOnDrop() {
        return onDrop != null;
    }
}
