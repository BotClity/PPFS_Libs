package com.ppfs.ppfs_libs.models.menu.slots;

import com.ppfs.ppfs_libs.models.menu.slots.actions.OnClick;
import com.ppfs.ppfs_libs.models.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ISlot {

    /**
     * Возвращает ItemStack, который представляет слот.
     * @return ItemStack.
     */
    ItemStack toItemStack();

    /**
     * Возвращает ItemStack с учётом данных игрока.
     * @param player игрок, чьи данные будут использоваться.
     * @return ItemStack.
     */
    ItemStack toItemStack(Player player);

    /**
     * Проверяет, есть ли у слота слушатель кликов.
     * @return true, если слушатель есть, иначе false.
     */
    boolean hasListener();

    /**
     * Устанавливает слушатель для кликов по слоту.
     * @param listener слушатель кликов.
     */
    ISlot setListener(OnClick listener);

    /**
     * Устанавливает количество предметов в слоте.
     * @param amount количество.
     */
    ISlot setAmount(int amount);

    /**
     * Устанавливает материал для слота.
     * @param material материал.
     */
    ISlot setMaterial(Material material);

    /**
     * Устанавливает название для слота.
     * @param displayName название.
     */
    ISlot setDisplayName(Message displayName);

    /**
     * Устанавливает описание для слота.
     * @param lore описание.
     */
    ISlot setLore(Message lore);
}