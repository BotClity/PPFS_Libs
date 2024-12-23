package com.ppfs.ppfs_libs.models.message;

import com.ppfs.ppfs_libs.PPFS_Libs;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Message {
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
            .hexColors()
            .build();
    private final List<String> rawMessages = new ArrayList<>();
    private final transient Placeholders placeholders = new Placeholders();
    private static final Pattern TAG_PATTERN = Pattern.compile("<(/?)([a-zA-Z_#0-9]+)>");

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
            rawMessages.add(serializer.serialize(component));
        }
    }

    public Message add(String... messages) {
        rawMessages.addAll(Arrays.asList(messages));
        return this;
    }

    public Message add(List<String> messages) {
        rawMessages.addAll(messages);
        return this;
    }

    public Message add(Message... msgs) {
        for (Message msg : msgs) {
            this.rawMessages.addAll(msg.rawMessages);
        }
        return this;
    }

    public Message add(Component... components){
        for (Component component: components){
            rawMessages.add(serializer.serialize(component));
        }
        return this;
    }

    public void addPlaceholders(String placeholder, String value) {
        placeholders.add(new Placeholders().add(placeholder, value));
    }

    public void addPlaceholders(String placeholder, String... values) {
        placeholders.add(new Placeholders().add(placeholder, values));
    }

    public void addPlaceholders(Placeholders placeholders) {
        this.placeholders.add(placeholders);
    }

    public Component getComponent() {
        Component result = Component.empty();
        for (String rawMessage : rawMessages) {
            List<String> parsedMessage = placeholders.apply(rawMessage);
            for (String str : parsedMessage) {
                result = result.append(parseTagsToComponent(str));
            }
        }
        return result;
    }

    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>();
        for (String rawMessage : rawMessages) {
            List<String> parsedMessage = placeholders.apply(rawMessage);
            for (String str : parsedMessage) {
                components.add(parseTagsToComponent(str));
            }
        }
        return components;
    }

    public void send(Audience audience) {
        for (String rawMessage : rawMessages) {
            List<String> parsedMessage = placeholders.apply(rawMessage);
            for (String str : parsedMessage) {
                Component component = parseTagsToComponent(str);
                audience.sendMessage(component);
            }
        }
    }

    public void send(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            send(player);
        } else {
            PPFS_Libs.getPaperLogger().warning("Player with UUID " + uuid + " is not online.");
        }
    }

    public void sendActionBar(Audience audience) {
        for (String rawMessage : rawMessages) {
            List<String> parsedMessage = placeholders.apply(rawMessage);
            for (String str : parsedMessage) {
                Component component = parseTagsToComponent(str);
                audience.sendActionBar(component);
            }
        }
    }

    public void sendActionBar(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            sendActionBar(player);
        }else {
            PPFS_Libs.getPaperLogger().warning("Player with UUID " + uuid + " is not online.");
        }
    }

    private Component parseTagsToComponent(String message) {
        Component result = Component.empty();
        result = result.style(style->style.decoration(TextDecoration.ITALIC, false));
        Deque<TextColor> colorStack = new ArrayDeque<>();
        Deque<TextDecoration> decorationStack = new ArrayDeque<>();

        Matcher matcher = TAG_PATTERN.matcher(message);
        int lastEnd = 0;

        while (matcher.find()) {
            String textBeforeTag = message.substring(lastEnd, matcher.start());
            if (!textBeforeTag.isEmpty()) {
                Component textComponent = Component.text(textBeforeTag);

                if (!colorStack.isEmpty()) {
                    textComponent = textComponent.color(colorStack.peek());
                }
                for (TextDecoration decoration : decorationStack) {
                    textComponent = textComponent.decorate(decoration);
                }
                result = result.append(textComponent);
            }

            String tagType = matcher.group(1);
            String tagName = matcher.group(2);

            if (tagType.isEmpty()) { // Открывающий тег
                TextColor color = getTextColor(tagName);
                if (color != null) {
                    colorStack.push(color);
                } else {
                    TextDecoration decoration = getTextDecoration(tagName);
                    if (decoration != null) {
                        decorationStack.push(decoration);
                    }
                }
            } else { // Закрывающий тег
                TextColor color = getTextColor(tagName);
                if (color != null && !colorStack.isEmpty()) {
                    colorStack.pop();
                } else {
                    TextDecoration decoration = getTextDecoration(tagName);
                    if (decoration != null && !decorationStack.isEmpty()) {
                        decorationStack.pop();
                    }
                }
            }
            lastEnd = matcher.end();
        }

        String remainingText = message.substring(lastEnd);
        if (!remainingText.isEmpty()) {
            Component textComponent = Component.text(remainingText);
            if (!colorStack.isEmpty()) {
                textComponent = textComponent.color(colorStack.peek());
            }
            for (TextDecoration decoration : decorationStack) {
                textComponent = textComponent.decorate(decoration);
            }
            result = result.append(textComponent);
        }
        return result;
    }

    private TextColor getTextColor(String tagName) {
        return switch (tagName.toLowerCase()) {
            case "red" -> TextColor.color(0xFF5555);
            case "green" -> TextColor.color(0x55FF55);
            case "blue" -> TextColor.color(0x5555FF);
            case "yellow" -> TextColor.color(0xFFFF55);
            case "aqua" -> TextColor.color(0x55FFFF);
            case "gold" -> TextColor.color(0xFFAA00);
            case "gray" -> TextColor.color(0xAAAAAA);
            case "dark_red" -> TextColor.color(0xAA0000);
            case "dark_green" -> TextColor.color(0x00AA00);
            case "dark_blue" -> TextColor.color(0x0000AA);
            case "dark_aqua" -> TextColor.color(0x00AAAA);
            case "dark_gray" -> TextColor.color(0x555555);
            case "dark_purple" -> TextColor.color(0xAA00AA);
            case "light_purple" -> TextColor.color(0xFF55FF);
            case "white" -> TextColor.color(0xFFFFFF);
            case "black" -> TextColor.color(0x000000);
            default -> null;
        };
    }

    private TextDecoration getTextDecoration(String tagName) {
        return switch (tagName) {
            case "bold" -> TextDecoration.BOLD;
            case "italic" -> TextDecoration.ITALIC;
            case "underline" -> TextDecoration.UNDERLINED;
            case "strikethrough" -> TextDecoration.STRIKETHROUGH;
            case "obfuscated" -> TextDecoration.OBFUSCATED;
            default -> null;
        };
    }
}
