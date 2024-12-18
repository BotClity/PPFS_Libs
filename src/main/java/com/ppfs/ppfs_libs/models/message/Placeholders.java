package com.ppfs.ppfs_libs.models.message;

import java.util.*;

public class Placeholders {
    private final Map<String, List<String>> placeholders = new HashMap<>();

    public Placeholders add(String key, String... values) {
        if (key == null || values == null) throw new RuntimeException("key or value is null " + key + " " + values);
        placeholders.put(key, List.of(values));
        return this;
    }

    public Placeholders add(String key, List<String> values) {
        if (key == null || values == null) throw new RuntimeException("key or values are null " + key + " " + values);
        placeholders.put(key, new ArrayList<>(values));
        return this;
    }

    public Placeholders add(Placeholders placeholders) {
        this.placeholders.putAll(placeholders.placeholders);
        return this;
    }

    public List<String> apply(String message) {
        if (message == null || message.isEmpty()) return Collections.emptyList();

        List<String> results = new ArrayList<>();
        results.add(message);

        for (Map.Entry<String, List<String>> entry : placeholders.entrySet()) {
            List<String> newResults = new ArrayList<>();
            for (String result : results) {
                if (result.contains("<" + entry.getKey() + ">")) {
                    for (String replacement : entry.getValue()) {
                        newResults.add(result.replace("<" + entry.getKey() + ">", replacement));
                    }
                } else {
                    newResults.add(result);
                }
            }
            results = newResults;
        }
        return results;
    }
}
