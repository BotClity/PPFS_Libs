package com.ppfs.ppfs_libs.models.menu.slots;

import com.google.common.collect.Sets;
import com.ppfs.ppfs_libs.listeners.menu.slots.SlotListener;
import com.ppfs.ppfs_libs.models.message.Message;
import com.ppfs.ppfs_libs.models.message.Placeholders;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Slot {
    private Message displayName;
    private Message lore;
    private Material material = Material.STONE;
    private transient SlotListener listener;
    private transient ItemMeta meta;
    private int position = -1;
    private int amount = 1;
    private int customModelData;
    private Map<Enchantment, Integer> enchantments = new ConcurrentHashMap<>();
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

    public Slot addDisplayName(Message additionalDisplayName) {
        if (this.displayName == null) {
            this.displayName = additionalDisplayName;
        } else {
            this.displayName.add(additionalDisplayName);
        }
        return this;
    }

    public boolean hasDisplayName() {
        return displayName != null;
    }

    public Slot setLore(String... lore) {
        if (this.lore == null) {
            this.lore = new Message(lore);
        } else {
            this.lore.add(lore);
        }
        return this;
    }

    public Slot setLore(Message lore) {
        this.lore = lore;
        return this;
    }

    public Slot addLore(Message lore) {
        if (this.lore == null) {
            this.lore = lore;
        } else {
            this.lore.add(lore);
        }
        return this;
    }

    public Slot addLore(String... lore) {
        if (this.lore == null) {
            this.lore = new Message(lore);
        } else {
            this.lore.add(lore);
        }
        return this;
    }

    public boolean hasLore() {
        return lore != null;
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

    public Slot clearEnchantments() {
        this.enchantments = new ConcurrentHashMap<>();
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
        if (itemFlags != null) {
            this.itemFlags.addAll(Arrays.asList(itemFlags));
        }
        return this;
    }

    public Slot setItemFlags(ItemFlag... itemFlags) {
        this.itemFlags = itemFlags != null ? Sets.newHashSet(itemFlags) : new HashSet<>();
        return this;
    }

    public Slot setItemFlags(@NotNull Set<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public Slot addItemFlags(Set<ItemFlag> itemFlags) {
        this.itemFlags.addAll(itemFlags);
        return this;
    }

    public static Slot from(ItemStack item) {
        return new Slot().fromItemStack(item);
    }

    public Slot fromItemStack(ItemStack item) {
        setMaterial(item.getType());
        setAmount(item.getAmount());

        setDisplayName(new Message(item.displayName()));

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;
        setMeta(meta);

        setEnchantments(meta.getEnchants());
        if (meta.lore() != null) {
            setLore(new Message(meta.lore().toArray(new Component[0])));
        }

        if (meta.hasCustomModelData()) {
            setCustomModelData(meta.getCustomModelData());
        }

        setItemFlags(meta.getItemFlags());
        return this;
    }

    public Placeholders getPlaceholders(HumanEntity player) {
        return placeholders;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);

        ItemMeta itemMeta = getMeta(item);

        item.setItemMeta(itemMeta);
        return item;
    }

    public ItemMeta getMeta(ItemStack item){
        ItemMeta meta = this.meta != null ? this.meta : item.getItemMeta();

        if (displayName != null) {
            displayName.addPlaceholders(getPlaceholders());
            meta.displayName(displayName.getComponent());
        }

        if (lore != null) {
            lore.addPlaceholders(getPlaceholders());
            meta.lore(lore.getComponents());
        }

        enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        if (!itemFlags.isEmpty()) {
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }

        if (customModelData != 0) {
            meta.setCustomModelData(customModelData);
        }

        return meta;
    }

    public ItemMeta getMeta(ItemStack item, HumanEntity player) {
        ItemMeta meta = this.meta != null ? this.meta : item.getItemMeta();

        if (displayName != null) {
            displayName.addPlaceholders(getPlaceholders(player));
            meta.displayName(displayName.getComponent());
        }

        if (lore != null) {
            lore.addPlaceholders(getPlaceholders(player));
            meta.lore(lore.getComponents());
        }

        enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        if (!itemFlags.isEmpty()) {
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }

        if (customModelData != 0) {
            meta.setCustomModelData(customModelData);
        }
        return meta;
    }

    public ItemStack toItemStack(HumanEntity player) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);

        ItemMeta itemMeta = getMeta(item, player);

        item.setItemMeta(itemMeta);
        return item;
    }

    public static class Builder {
        private final Slot slot = new Slot();

        public Builder material(Material material) {
            slot.setMaterial(material);
            return this;
        }

        public Builder displayName(Message displayName) {
            slot.setDisplayName(displayName);
            return this;
        }

        public Builder lore(Message lore) {
            slot.setLore(lore);
            return this;
        }

        public Builder enchantment(Enchantment enchantment, int level) {
            slot.addEnchantment(enchantment, level);
            return this;
        }

        public Slot build() {
            return slot;
        }
    }
}
