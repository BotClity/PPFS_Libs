// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs.models.menu.slots;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

@Getter
public class HeadSlot extends Slot {
    private String textureValue = null;
    private OfflinePlayer owner = null;

    @Override
    @Deprecated
    public ItemStack toItemStack() {
        return setupHeadValue(super.toItemStack());
    }

    @Override
    public ItemStack toItemStack(HumanEntity player) {
        return setupHeadValue(super.toItemStack());
    }



    private ItemStack setupHeadValue(ItemStack item){
        if (textureValue != null && item.getType() == Material.PLAYER_HEAD && owner == null) {
            NBT.modify(item, nbt -> {
                ReadWriteNBT skullOwnerCompound = nbt.getOrCreateCompound("SkullOwner");

                skullOwnerCompound.setUUID("Id", UUID.randomUUID());

                skullOwnerCompound.getOrCreateCompound("Properties")
                        .getCompoundList("textures")
                        .addCompound()
                        .setString("Value", textureValue);
            });
        }
        return item;
    }

    @Override
    public ItemMeta getMeta(ItemStack item) {
        return setupHeadOwner(super.getMeta(item));
    }

    @Override
    public ItemMeta getMeta(ItemStack item, HumanEntity player) {
        return setupHeadOwner(super.getMeta(item));
    }

    private ItemMeta setupHeadOwner(ItemMeta meta){
        if (meta instanceof SkullMeta skullMeta && owner != null)
            skullMeta.setOwningPlayer(owner);
        return meta;
    }

    /**
    Устанавливает скин на голову из value
    @param value значение скина
     */
    public Slot setHeadValue(String value){
        this.textureValue = value;
        return this;
    }

    /**
     Устанавливает скин на голову из Offline player
     @param player Офлайн игрок
     */
    public void setHeadOwner (OfflinePlayer player){
            owner = player;
        }
    }
