package com.ppfs.ppfs_libs.models.message;

import java.util.HashMap;
import java.util.Map;

public class Placeholders {
    private final Map<String, String> placeholders = new HashMap<>();

    public Placeholders add(String key, String value) {
        if (key == null || value == null)throw new RuntimeException("key or value is null "+key+" "+value);
        placeholders.put(key, value);
        return this;
    }

    public Placeholders add(Placeholders placeholders) {
        this.placeholders.putAll(placeholders.placeholders);
        return this;
    }

    public String apply(String message) {
        if (message == null || message.isEmpty()) return "";
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("<" + entry.getKey() + ">", entry.getValue());
        }
        return result;
    }
}
