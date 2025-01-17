package com.ppfs.ppfs_libs.models.message;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Message {
    private static final boolean placeholdersApi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    private static final Logger log = LoggerFactory.getLogger(Message.class);
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().build();
    private final List<String> rawMessages = new ArrayList<>();
    private final transient Placeholders placeholders = new Placeholders();
    private static final Pattern TAG_PATTERN = Pattern.compile("<(/?)([a-zA-Z_#0-9]+)>");

    public Message(String... messages) {
        rawMessages.addAll(Arrays.asList(messages));
    }

    public Message(List<String> messages) {
        rawMessages.addAll(messages);
    }

    public Message(Component... components) {
        for (Component component : components) {
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

    public void addPlaceholders(String placeholder, String... values) {
        placeholders.add(new Placeholders().add(placeholder, values));
    }

    public void addPlaceholders(Placeholders placeholders) {
        this.placeholders.add(placeholders);
    }

    public Component getComponent() {
        return getParsedComponents().stream().reduce(Component.empty(), Component::append);
    }

    public Component getComponent(Player player) {
        return getParsedComponents(player).stream().reduce(Component.empty(), Component::append);
    }

    public List<Component> getComponents() {
        return getParsedComponents();
    }

    public List<Component> getComponents(Player player) {
        return getParsedComponents(player);
    }

    private List<Component> getParsedComponents() {
        return parseMessages(rawMessages, null);
    }

    private List<Component> getParsedComponents(Player player) {
        return parseMessages(rawMessages, player);
    }

    private List<Component> parseMessages(List<String> messages, Player player) {
        List<Component> components = new ArrayList<>();
        for (String rawMessage : messages) {
            List<String> parsedMessage = placeholders.apply(rawMessage);
            parsedMessage = replacePlaceholders(parsedMessage, player);
            for (String str : parsedMessage) {
                components.add(parseTagsToComponent(str));
            }
        }
        return components;
    }

    private List<String> replacePlaceholders(List<String> messages, Player player) {
        return messages.stream().map(msg -> replacePlaceholders(msg, player)).toList();
    }

    private String replacePlaceholders(String message, Player player) {
        if (player != null && placeholdersApi) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public void send(Audience audience) {
        rawMessages.forEach(rawMessage -> sendToAudience(audience, rawMessage));
    }

    public void send(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            send(player);
        } else {
            log.warn("Player with UUID {} is not online.", uuid);
        }
    }

    public void sendActionBar(Audience audience) {
        rawMessages.forEach(rawMessage -> sendActionBarToAudience(audience, rawMessage));
    }

    public void sendActionBar(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            sendActionBar(player);
        } else {
            log.warn("Player with UUID {} is not online.", uuid);
        }
    }

    private void sendToAudience(Audience audience, String rawMessage) {
        List<String> parsedMessage = placeholders.apply(rawMessage);
        parsedMessage.forEach(str -> audience.sendMessage(parseTagsToComponent(str)));
    }

    private void sendActionBarToAudience(Audience audience, String rawMessage) {
        List<String> parsedMessage = placeholders.apply(rawMessage);
        parsedMessage.forEach(str -> audience.sendActionBar(parseTagsToComponent(str)));
    }

    private Component parseTagsToComponent(String message) {
        Component result = Component.empty();
        result = result.style(style -> style.decoration(TextDecoration.ITALIC, false));
        Deque<TextColor> colorStack = new ArrayDeque<>();
        Deque<TextDecoration> decorationStack = new ArrayDeque<>();

        Matcher matcher = TAG_PATTERN.matcher(message);
        int lastEnd = 0;

        while (matcher.find()) {
            String textBeforeTag = message.substring(lastEnd, matcher.start());
            if (!textBeforeTag.isEmpty()) {
                Component textComponent = Component.text(textBeforeTag);
                applyStyles(colorStack, decorationStack, textComponent);
                result = result.append(textComponent);
            }

            String tagType = matcher.group(1);
            String tagName = matcher.group(2);

            if (tagType.isEmpty()) {
                handleOpeningTag(colorStack, decorationStack, tagName);
            } else {
                handleClosingTag(colorStack, decorationStack, tagName);
            }
            lastEnd = matcher.end();
        }

        String remainingText = message.substring(lastEnd);
        if (!remainingText.isEmpty()) {
            Component textComponent = Component.text(remainingText);
            applyStyles(colorStack, decorationStack, textComponent);
            result = result.append(textComponent);
        }
        return result;
    }

    private void applyStyles(Deque<TextColor> colorStack, Deque<TextDecoration> decorationStack, Component textComponent) {
        if (!colorStack.isEmpty()) {
            textComponent = textComponent.color(colorStack.peek());
        }
        for (TextDecoration decoration : decorationStack) {
            textComponent = textComponent.decorate(decoration);
        }
    }

    private void handleOpeningTag(Deque<TextColor> colorStack, Deque<TextDecoration> decorationStack, String tagName) {
        TextColor color = getTextColor(tagName);
        if (color != null) {
            colorStack.push(color);
        } else {
            TextDecoration decoration = getTextDecoration(tagName);
            if (decoration != null) {
                decorationStack.push(decoration);
            }
        }
    }

    private void handleClosingTag(Deque<TextColor> colorStack, Deque<TextDecoration> decorationStack, String tagName) {
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
