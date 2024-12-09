package com.ppfs.ppfs_libs.models.message;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Message {
    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder()
            .character('§')
            .hexColors()
            .build();
    private final List<String> rawMessages = new ArrayList<>();
    private final transient Placeholders placeholders = new Placeholders();
    private static final Pattern COLOR_TAG_PATTERN = Pattern.compile("<(red|green|blue|yellow|gold|aqua|white|black|gray|dark_gray|dark_red|dark_green|dark_blue|dark_aqua|dark_purple|light_purple)>");
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");

    public Message(String message) {
        this.rawMessages.add(message);
    }

    public Message(String... messages) {
        this.rawMessages.addAll(Arrays.asList(messages));
    }

    public Message(List<String> messages) {
        this.rawMessages.addAll(messages);
    }

    public Message(Component... components){
        for (Component component: components){
            rawMessages.add(legacyComponentSerializer.serialize(component));
        }
    }


    // Добавить сообщение
    public Message add(String... messages) {
        rawMessages.addAll(Arrays.asList(messages));
        return this;
    }

    public Message add(List<String> messages) {
        rawMessages.addAll(messages);
        return this;
    }

    public Message add(Component component){
        rawMessages.add(legacyComponentSerializer.serialize(component));
        return this;
    }

    public Message add(Message... messages) {
        for (Message message : messages) {
            rawMessages.addAll(message.rawMessages);
        }
        return this;
    }

    public void addPlaceholders(String placeholder, String value) {
        placeholders.add(new Placeholders().add(placeholder, value));
    }

    public void addPlaceholders(Placeholders placeholders) {
        this.placeholders.add(placeholders);
    }

    public Component getComponent() {
        Component result = Component.empty();
        for (String rawMessage : rawMessages) {
            String parsedMessage = placeholders.apply(rawMessage);
            parsedMessage = applyHexAndColorTags(parsedMessage);
            result = result.append(legacyComponentSerializer.deserialize(parsedMessage));
        }
        return result;
    }

    public void send(Audience audience) {
        for (String rawMessage : rawMessages) {
            String parsedMessage = placeholders.apply(rawMessage);
            parsedMessage = applyHexAndColorTags(parsedMessage);
            Component component = legacyComponentSerializer.deserialize(parsedMessage);
            audience.sendMessage(component);
        }
    }

    public void send(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            send(player);
        } else {
            System.out.println("Player with UUID " + uuid + " is not online.");
        }
    }

    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>();
        for (String rawMessage : rawMessages) {
            String parsedMessage = placeholders.apply(rawMessage);
            parsedMessage = applyHexAndColorTags(parsedMessage);
            Component component = legacyComponentSerializer.deserialize(parsedMessage);
            components.add(component);
        }
        return components;
    }

    public String toString(){
        return legacyComponentSerializer.serialize(getComponent());
    }

    private String applyColors(String message){
        return message.replace('&', '§');
    }

    private String applyHexAndColorTags(String message) {
        message = applyHexColors(message);
        message = applyColors(message);

        Matcher matcher = COLOR_TAG_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String colorName = matcher.group(1).toLowerCase(); // Используем нижний регистр
            String minecraftCode = getMinecraftColorCode(colorName);
            if (minecraftCode != null) {
                matcher.appendReplacement(buffer, "§" + minecraftCode);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String getMinecraftColorCode(String colorName) {
        switch (colorName) {
            case "red":
                return "c";
            case "green":
                return "a";
            case "blue":
                return "9";
            case "yellow":
                return "e";
            case "gold":
                return "6";
            case "aqua":
                return "b";
            case "white":
                return "f";
            case "black":
                return "0";
            case "gray":
                return "7";
            case "dark_gray":
                return "8";
            case "dark_red":
                return "4";
            case "dark_green":
                return "2";
            case "dark_blue":
                return "1";
            case "dark_aqua":
                return "3";
            case "dark_purple":
                return "5";
            case "light_purple":
                return "d";
            default:
                return null;
        }
    }


    private String applyHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, "§x" + hexCode.replaceAll("\\.", "§$0"));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
