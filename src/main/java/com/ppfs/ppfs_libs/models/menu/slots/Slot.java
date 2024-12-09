package com.ppfs.ppfs_libs.models.menu.slots;


import com.google.common.collect.Sets;
import com.ppfs.ppfs_libs.listeners.menu.slots.SlotListener;
import com.ppfs.ppfs_libs.models.message.Message;
import com.ppfs.ppfs_libs.models.message.Placeholders;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Getter
@Setter
public class Slot {

    private boolean interactDisabled = true;
    private Message displayName;
    private Message lore;
    private Material material = Material.STONE;
    private transient SlotListener listener;
    private transient ItemMeta meta;
    private int position = -1;
    private int amount;
    private int customModelData;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<ItemFlag> itemFlags = new HashSet<>();
    private Placeholders placeholders = new Placeholders();


    public Slot() {
    }

    public Slot(Material material) {
        this.material = material;
    }

    public Slot(Material material, Message displayName, Message lore) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    public Slot setMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    public Slot setDisplayName(String displayName) {
        this.displayName = new Message(displayName);
        return this;
    }

    public Slot setDisplayName(Message displayName) {
        this.displayName = displayName;
        return this;
    }

    public boolean hasDisplayName(){
        return displayName != null;
    }

    public Slot setLore(Message lore) {
        this.lore = lore;
        return this;
    }

    public Slot setLore(String... lore) {
        this.lore = new Message(lore);
        return this;
    }

    public boolean hasLore(){
        return lore != null;
    }

    public Slot addLore(String... lore) {
        this.lore.add(lore);
        return this;
    }

    public Slot addLore(Message lore) {
        this.lore.add(lore);
        return this;
    }

    public Slot setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public boolean hasListener() {
        return listener != null;
    }

    public Slot setListener(SlotListener listener) {
        this.listener = listener;
        return this;
    }

    public Slot setPosition(int position) {
        this.position = position;
        return this;
    }

    public Slot addAmount(int amount) {
        this.amount += amount;
        return this;
    }

    public Slot setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public Slot setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public Slot addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public Slot clearEnchantments(){
        this.enchantments.clear();
        return this;
    }

    public Slot removeEnchantment(Enchantment enchantment) {
        this.enchantments.remove(enchantment);
        return this;
    }

    public Slot setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public Slot resetCustomModelData() {
        this.customModelData = 0;
        return this;
    }

    public Slot addItemFlag(ItemFlag... itemFlags) {
        this.itemFlags.addAll(Arrays.asList(itemFlags));
        return this;
    }

    public Slot setItemFlags(ItemFlag... itemFlags) {
        if (itemFlags == null) return this;
        this.itemFlags = Sets.newHashSet(itemFlags);
        return this;
    }

    public Slot setItemFlags(Set<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public static Slot from(ItemStack item) {
        return new Slot().fromItemStack(item);
    }

    public Slot fromItemStack(ItemStack item){

        setMaterial(item.getType());
        setAmount(item.getAmount());

        setDisplayName(new Message(item.displayName()));


        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;

        setEnchantments(meta.getEnchants());

        List<Component> loreList = meta.lore();

        if (loreList != null) {
            Component[] lore = loreList.toArray(new Component[0]);
            setLore(new Message(lore));
        }

        if (meta.hasCustomModelData()) {
            setCustomModelData(meta.getCustomModelData());
        }

        setItemFlags(meta.getItemFlags());

        return this;
    }


    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material);

        item.setAmount(amount);

        ItemMeta meta = this.meta == null ? item.getItemMeta() : this.meta;
        if (meta == null) return item;

        if (displayName != null) {
            displayName.addPlaceholders(placeholders);
            meta.displayName(displayName.getComponent());
        }

        if (lore != null) {
            lore.addPlaceholders(placeholders);
            meta.lore(lore.getComponents());
        }

        if (!enchantments.isEmpty())
            enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));

        if (!itemFlags.isEmpty())
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));

        if (customModelData != 0)
            meta.setCustomModelData(0);

        item.setItemMeta(meta);
        return item;

    }
}
