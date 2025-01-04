// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs.models.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ppfs.ppfs_libs.models.menu.slots.Slot;
import com.ppfs.ppfs_libs.models.menu.slots.SlotAdapter;
import com.ppfs.ppfs_libs.models.message.Message;
import com.ppfs.ppfs_libs.models.message.MessageAdapter;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public abstract class ConfigJSON{

    @Getter
    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageAdapter())
            .registerTypeAdapter(Slot.class, new SlotAdapter())
            .disableHtmlEscaping()
            .setLenient()
            .setPrettyPrinting()
            .create();
    private static final Logger log = LoggerFactory.getLogger(ConfigJSON.class);

    protected transient File file;
    protected transient Class<? extends ConfigJSON> clazz;

    /**
     * Статический метод для сохранения экземпляра конфигурации в соответствующий файл.
     */
    public static <T extends ConfigJSON> void save(T instance) throws IOException {
        if (instance.file == null) {
            log.error("Ссылка на файл отсутствует в экземпляре конфигурации.");
            throw new IllegalStateException("Ссылка на файл отсутствует в экземпляре конфигурации.");
        }

        try (FileWriter writer = new FileWriter(instance.file, StandardCharsets.UTF_8)) {
            gson.toJson(instance, writer);
            log.info("Конфигурация успешно сохранена в файл: {}", instance.file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при сохранении конфигурации в файл: {}", instance.file.getAbsolutePath(), e);
            throw e;
        }
    }

    /**
     * Нестатический метод для сохранения текущего экземпляра конфигурации.
     */
    public void save() {
        try {
            save(this);
        } catch (IOException e) {
            log.error("Ошибка при сохранении конфигурации.", e);
            throw new RuntimeException("Ошибка при сохранении конфигурации.", e);
        }
    }

    /**
     * Статический метод для загрузки экземпляра конфигурации из файла.
     */
    public static <T extends ConfigJSON> T load(File file, Class<T> clazz) {
        try {
            if (!file.exists()) {
                log.warn("Файл конфигурации не найден, создается новый: {}", file.getAbsolutePath());
                T instance = clazz.getDeclaredConstructor().newInstance();
                instance.setFile(file);
                instance.setClazz(clazz);
                save(instance);
                return instance;
            }

            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

            T instance = gson.fromJson(content, clazz);

            if (instance != null) {
                instance.setFile(file);
                instance.setClazz(clazz);
            }
            return instance;
        } catch (JsonSyntaxException e) {
            log.error("Ошибка синтаксиса JSON: {}", e.getMessage());
            log.error("Содержимое JSON, вызвавшее ошибку:\n{}", getFaultyJsonSnippet(file));
            throw new RuntimeException("Ошибка синтаксиса JSON в файле: " + file.getName(), e);
        } catch (Exception e) {
            log.error("Общая ошибка при загрузке конфигурации из файла: {}", file.getAbsolutePath(), e);
            throw new RuntimeException("Ошибка при загрузке конфигурации.", e);
        }
    }

    private static String getFaultyJsonSnippet(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            return content.trim();
        } catch (IOException e) {
            return "Не удалось прочитать файл JSON: " + file.getAbsolutePath();
        }
    }



    /**
     * Загрузка экземпляра конфигурации по имени файла.
     */
    public static <T extends ConfigJSON> T load(Plugin plugin, String fileName, Class<T> clazz) {
        if (!fileName.endsWith(".json")) fileName = fileName.concat(".json");
        return load(new File(plugin.getDataFolder(), fileName), clazz);
    }

    protected void setFile(File file) {
        this.file = file;
    }

    protected void setClazz(Class<? extends ConfigJSON> clazz) {
        this.clazz = clazz;
    }

    /**
     * Метод для перезагрузки конфигурации из файла.
     */
    public void reload() {
        if (this.file == null || this.clazz == null) {
            log.error("Ссылка на файл или класс отсутствует, невозможно перезагрузить конфигурацию.");
            throw new IllegalStateException("Ссылка на файл или класс отсутствует, невозможно перезагрузить.");
        }
        try {
            log.info("Перезагрузка конфигурации из файла: {}", file.getAbsolutePath());
            ConfigJSON newInstance = load(this.file, this.clazz);
            if (newInstance != null) {
                this.copyFrom(newInstance);
            }
        } catch (Exception e) {
            log.error("Ошибка при перезагрузке конфигурации из файла: {}", file.getAbsolutePath(), e);
            throw e;
        }
    }

    /**
     * Метод для копирования всех значений полей из другого объекта.
     */
    protected void copyFrom(ConfigJSON other) {
        if (other == null) {
            log.error("Другой экземпляр конфигурации не может быть null.");
            throw new IllegalArgumentException("Другой экземпляр не может быть null.");
        }

        Class<?> clazz = this.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                        continue;
                    }
                    Object value = field.get(other);
                    field.set(this, value);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    log.error("Ошибка при копировании поля: {}", field.getName(), e);
                    throw new RuntimeException("Не удалось скопировать поле: " + field.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
