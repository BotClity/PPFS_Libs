package com.ppfs.ppfs_libs.models.menu.slots;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.lang.reflect.Field;
import java.util.UUID;


public class HeadSlot extends Slot {
    public void setHeadValue(String value){
        ItemMeta meta = getMeta();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", value));
        if (meta!= null){
            Field field;
            try{
                field = meta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(meta, gameProfile);
            }catch (IllegalAccessException | NoSuchFieldException e){
                Bukkit.getLogger().warning("Failed to set head value");
            }
        }
        setMeta(meta);
    }
    public void setHeadOwner(OfflinePlayer player){
        SkullMeta meta = (SkullMeta) getMeta();
        meta.setOwningPlayer(player);
        setMeta(meta);
    }
}
