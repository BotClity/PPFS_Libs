// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs.models.menu.slots;

import com.google.common.collect.Sets;
import com.ppfs.ppfs_libs.models.menu.slots.actions.OnClick;
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
    private transient OnClick listener;
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

    /**
     * Устанавливает мета-данные для слота.
     * @param meta объект ItemMeta.
     * @return обновленный экземпляр Slot.
     */
    public Slot setMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    /**
     * Устанавливает название слота в виде строки.
     * @param displayName название.
     * @return обновленный экземпляр Slot.
     */
    public Slot setDisplayName(String displayName) {
        this.displayName = new Message(displayName);
        return this;
    }

    /**
     * Устанавливает название слота в виде объекта Message.
     * @param displayName объект Message.
     * @return обновленный экземпляр Slot.
     */
    public Slot setDisplayName(Message displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Добавляет дополнительное название к текущему названию слота.
     * @param additionalDisplayName объект Message.
     * @return обновленный экземпляр Slot.
     */
    public Slot addDisplayName(Message additionalDisplayName) {
        if (this.displayName == null) {
            this.displayName = additionalDisplayName;
        } else {
            this.displayName.add(additionalDisplayName);
        }
        return this;
    }

    /**
     * Проверяет, установлено ли название для слота.
     * @return true, если название установлено, иначе false.
     */
    public boolean hasDisplayName() {
        return displayName != null;
    }

    /**
     * Устанавливает описание (lore) слота в виде строк.
     * @param lore описание.
     * @return обновленный экземпляр Slot.
     */
    public Slot setLore(String... lore) {
        if (this.lore == null) {
            this.lore = new Message(lore);
        } else {
            this.lore.add(lore);
        }
        return this;
    }

    /**
     * Устанавливает описание (lore) слота в виде объекта Message.
     * @param lore объект Message.
     * @return обновленный экземпляр Slot.
     */
    public Slot setLore(Message lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Добавляет дополнительное описание к текущему описанию слота.
     * @param lore объект Message.
     * @return обновленный экземпляр Slot.
     */
    public Slot addLore(Message lore) {
        if (this.lore == null) {
            this.lore = lore;
        } else {
            this.lore.add(lore);
        }
        return this;
    }

    /**
     * Добавляет дополнительное описание к текущему описанию слота в виде строк.
     * @param lore строки описания.
     * @return обновленный экземпляр Slot.
     */
    public Slot addLore(String... lore) {
        if (this.lore == null) {
            this.lore = new Message(lore);
        } else {
            this.lore.add(lore);
        }
        return this;
    }

    /**
     * Проверяет, установлено ли описание для слота.
     * @return true, если описание установлено, иначе false.
     */
    public boolean hasLore() {
        return lore != null;
    }

    /**
     * Устанавливает материал для слота.
     * @param material материал.
     * @return обновленный экземпляр Slot.
     */
    public Slot setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Проверяет, установлен ли слушатель для слота.
     * @return true, если слушатель установлен, иначе false.
     */
    public boolean hasListener() {
        return listener != null;
    }

    /**
     * Устанавливает слушатель кликов для слота.
     * @param listener объект OnClick.
     * @return обновленный экземпляр Slot.
     */
    public Slot setListener(OnClick listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Устанавливает позицию слота в меню.
     * @param position позиция.
     * @return обновленный экземпляр Slot.
     */
    public Slot setPosition(int position) {
        this.position = position;
        return this;
    }

    /**
     * Увеличивает количество предметов в слоте.
     * @param amount количество для добавления.
     * @return обновленный экземпляр Slot.
     */
    public Slot addAmount(int amount) {
        this.amount += amount;
        return this;
    }

    /**
     * Устанавливает количество предметов в слоте.
     * @param amount количество.
     * @return обновленный экземпляр Slot.
     */
    public Slot setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Устанавливает зачарования для слота.
     * @param enchantments карта зачарований.
     * @return обновленный экземпляр Slot.
     */
    public Slot setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    /**
     * Добавляет зачарование к слоту.
     * @param enchantment зачарование.
     * @param level уровень зачарования.
     * @return обновленный экземпляр Slot.
     */
    public Slot addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Удаляет все зачарования из слота.
     * @return обновленный экземпляр Slot.
     */
    public Slot clearEnchantments() {
        this.enchantments = new ConcurrentHashMap<>();
        return this;
    }

    /**
     * Удаляет конкретное зачарование из слота.
     * @param enchantment зачарование для удаления.
     * @return обновленный экземпляр Slot.
     */
    public Slot removeEnchantment(Enchantment enchantment) {
        this.enchantments.remove(enchantment);
        return this;
    }

    /**
     * Устанавливает пользовательские данные модели для слота.
     * @param customModelData данные модели.
     * @return обновленный экземпляр Slot.
     */
    public Slot setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    /**
     * Сбрасывает пользовательские данные модели до значения по умолчанию (0).
     * @return обновленный экземпляр Slot.
     */
    public Slot resetCustomModelData() {
        this.customModelData = 0;
        return this;
    }

    /**
     * Добавляет флаги предмета к слоту.
     * @param itemFlags флаги предмета.
     * @return обновленный экземпляр Slot.
     */
    public Slot addItemFlag(ItemFlag... itemFlags) {
        if (itemFlags != null) {
            this.itemFlags.addAll(Arrays.asList(itemFlags));
        }
        return this;
    }

    /**
     * Устанавливает флаги предмета для слота.
     * @param itemFlags флаги предмета.
     * @return обновленный экземпляр Slot.
     */
    public Slot setItemFlags(ItemFlag... itemFlags) {
        this.itemFlags = itemFlags != null ? Sets.newHashSet(itemFlags) : new HashSet<>();
        return this;
    }

    /**
     * Устанавливает флаги предмета для слота с использованием множества.
     * @param itemFlags множество флагов предмета.
     * @return обновленный экземпляр Slot.
     */
    public Slot setItemFlags(@NotNull Set<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    /**
     * Добавляет множество флагов предмета к текущим флагам.
     * @param itemFlags множество флагов.
     * @return обновленный экземпляр Slot.
     */
    public Slot addItemFlags(Set<ItemFlag> itemFlags) {
        this.itemFlags.addAll(itemFlags);
        return this;
    }

    /**
     * Создает экземпляр слота из ItemStack.
     * @param item объект ItemStack.
     * @return новый экземпляр Slot.
     */
    public static Slot from(ItemStack item) {
        return new Slot().fromItemStack(item);
    }

    /**
     * Заполняет данные слота из ItemStack.
     * @param item объект ItemStack.
     * @return обновленный экземпляр Slot.
     */
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

    /**
     * Возвращает плейсхолдеры (placeholders).
     * @param player объект HumanEntity (игрок).
     * @return заполнители для слота.
     */
    public Placeholders getPlaceholders(HumanEntity player) {
        return placeholders;
    }

    /**
     * Преобразует данные слота в ItemStack.
     * @return объект ItemStack.
     */
    @Deprecated
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);

        ItemMeta itemMeta = getMeta(item);

        item.setItemMeta(itemMeta);
        return item;
    }

    /**
     * Генерирует мета-данные (ItemMeta) для указанного ItemStack.
     * @param item объект ItemStack.
     * @return объект ItemMeta.
     */
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

    /**
     * Генерирует мета-данные (ItemMeta) для указанного ItemStack.
     * @param item объект ItemStack.
     * @param player объект HumanEntity (игрок).
     * @return объект ItemMeta.
     */
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

    /**
     * Преобразует данные слота в ItemStack.
     * @param player объект HumanEntity (игрок).
     * @return объект ItemStack.
     */
    public ItemStack toItemStack(HumanEntity player) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);

        ItemMeta itemMeta = getMeta(item, player);

        item.setItemMeta(itemMeta);
        return item;
    }

    /**
     * Возвращает слушатель кликов для слота.
     * Если слушатель не установлен, возвращается слушатель по умолчанию, который ничего не делает.
     * @return объект OnClick.
     */
    public OnClick getListener(){
        if (listener == null){
            return event -> false;
        }
        return listener;
    }

    /**
     * Вложенный класс для построения (Builder) экземпляров Slot.
     */
    public static class Builder {
        private final Slot slot = new Slot();

        /**
         * Устанавливает материал для слота.
         * @param material материал.
         * @return экземпляр Builder.
         */
        public Builder material(Material material) {
            slot.setMaterial(material);
            return this;
        }

        /**
         * Устанавливает название для слота.
         * @param displayName объект Message.
         * @return экземпляр Builder.
         */
        public Builder displayName(Message displayName) {
            slot.setDisplayName(displayName);
            return this;
        }

        /**
         * Устанавливает описание для слота.
         * @param lore объект Message.
         * @return экземпляр Builder.
         */
        public Builder lore(Message lore) {
            slot.setLore(lore);
            return this;
        }

        /**
         * Добавляет зачарование к слоту.
         * @param enchantment зачарование.
         * @param level уровень зачарования.
         * @return экземпляр Builder.
         */
        public Builder enchantment(Enchantment enchantment, int level) {
            slot.addEnchantment(enchantment, level);
            return this;
        }

        /**
         * Создает и возвращает настроенный экземпляр Slot.
         * @return объект Slot.
         */
        public Slot build() {
            return slot;
        }
    }
}
