package com.ppfs.ppfs_libs.models.menu.slots;

import com.google.gson.*;
import com.ppfs.ppfs_libs.models.message.Message;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SlotAdapter implements JsonDeserializer<Slot>, JsonSerializer<Slot> {
    @Override
    public Slot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Slot slot = new Slot();

        slot.setMaterial(Material.valueOf(jsonObject.get("material").getAsString()));
        slot.setAmount(jsonObject.get("amount").getAsInt());
        slot.setPosition(jsonObject.get("position").getAsInt());
        slot.setCustomModelData(jsonObject.get("customModelData").getAsInt());

        if (jsonObject.has("displayName")) {
            Message name = context.deserialize(jsonObject.get("displayName"), Message.class);
            slot.setDisplayName(name);
        }
        if (jsonObject.has("lore")) {
            Message lore = context.deserialize(jsonObject.get("lore"), Message.class);
            slot.setLore(lore);
        }

        if (jsonObject.has("enchantments")) {
            JsonObject enchantmentsJson = jsonObject.getAsJsonObject("enchantments");
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : enchantmentsJson.entrySet()) {
                Enchantment enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(entry.getKey()));
                if (enchantment != null) {
                    enchantments.put(enchantment, entry.getValue().getAsInt());
                }
            }
            slot.setEnchantments(enchantments);
        }

        if (jsonObject.has("itemFlags")) {
            JsonArray itemFlagsJson = jsonObject.getAsJsonArray("itemFlags");
            Set<ItemFlag> itemFlags = new HashSet<>();
            for (JsonElement element : itemFlagsJson) {
                itemFlags.add(ItemFlag.valueOf(element.getAsString()));
            }
            slot.setItemFlags(itemFlags);
        }


        return slot;
    }

    @Override
    public JsonElement serialize(Slot src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.addProperty("material", src.getMaterial().name());
        json.addProperty("amount", src.getAmount());
        json.addProperty("position", src.getPosition());
        json.addProperty("customModelData", src.getCustomModelData());

        if (src.hasDisplayName()) {
            json.add("displayName", context.serialize(src.getDisplayName()));
        }
        if (src.hasLore()) {
            json.add("lore", context.serialize(src.getLore()));
        }

        JsonObject enchantments = new JsonObject();
        for (Map.Entry<Enchantment, Integer> enchant : src.getEnchantments().entrySet()) {
            enchantments.addProperty(enchant.getKey().getKey().toString(), enchant.getValue());
        }
        json.add("enchantments", enchantments);

        if (!src.getItemFlags().isEmpty()){
            JsonArray flags = new JsonArray();

            for (ItemFlag itemFlag: src.getItemFlags()){
                flags.add(itemFlag.name());
            }

            json.add("itemFlags", flags);

        }

        return json;
    }
}
