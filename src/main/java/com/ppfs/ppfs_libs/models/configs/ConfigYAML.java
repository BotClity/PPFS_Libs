// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs.models.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public abstract class ConfigYAML{


    private static final Logger log = LoggerFactory.getLogger(ConfigYAML.class);
    protected transient File file;
    protected transient FileConfiguration yamlConfig;
    protected transient Class<? extends ConfigYAML> clazz;

    /**
     * Статический метод для сохранения экземпляра конфигурации в YAML файл.
     */
    public static <T extends ConfigYAML> void save(T instance) {
        if (instance.file == null || instance.yamlConfig == null) {
            log.error("Ссылка на файл или FileConfiguration отсутствует в экземпляре конфигурации.");
            throw new IllegalStateException("Ссылка на файл или FileConfiguration отсутствует в экземпляре конфигурации.");
        }

        try {
            instance.yamlConfig.save(instance.file);
            log.info("Конфигурация успешно сохранена в файл: {}", instance.file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при сохранении конфигурации в файл: {}", instance.file.getAbsolutePath(), e);
            throw new RuntimeException("Ошибка при сохранении конфигурации.", e);
        }
    }

    /**
     * Нестатический метод для сохранения текущего экземпляра конфигурации.
     */
    public void save() {
        save(this);
    }

    /**
     * Статический метод для загрузки экземпляра конфигурации из YAML файла.
     */
    public static <T extends ConfigYAML> T load(File file, Class<T> clazz) {
        try {
            if (!file.exists()) {
                log.warn("Файл конфигурации не найден, создается новый: {}", file.getAbsolutePath());
                T instance = clazz.getDeclaredConstructor().newInstance();
                instance.setFile(file);
                instance.yamlConfig = YamlConfiguration.loadConfiguration(file);
                instance.setClazz(clazz);
                save(instance);
                return instance;
            }

            FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
            T instance = clazz.getDeclaredConstructor().newInstance();
            instance.setFile(file);
            instance.yamlConfig = yamlConfig;
            instance.setClazz(clazz);

            log.info("Конфигурация успешно загружена из файла: {}", file.getAbsolutePath());
            return instance;
        } catch (Exception e) {
            log.error("Ошибка при загрузке конфигурации из файла: {}", file.getAbsolutePath(), e);
            throw new RuntimeException("Ошибка при загрузке конфигурации.", e);
        }
    }

    /**
     * Загрузка экземпляра конфигурации по имени файла.
     */
    public static <T extends ConfigYAML> T load(Plugin plugin, String fileName, Class<T> clazz) {
        if (!fileName.endsWith(".yml")) fileName = fileName.concat(".yml");
        return load(new File(plugin.getDataFolder(), fileName), clazz);
    }

    protected void setFile(File file) {
        this.file = file;
    }

    protected void setClazz(Class<? extends ConfigYAML> clazz) {
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
            ConfigYAML newInstance = load(this.file, this.clazz);
            if (newInstance != null) {
                this.copyFrom(newInstance);
            }
        } catch (Exception e) {
            log.error("Ошибка при перезагрузке конфигурации из файла: {}", file.getAbsolutePath(), e);
            throw new RuntimeException("Ошибка при перезагрузке конфигурации.", e);
        }
    }

    /**
     * Метод для копирования всех значений полей из другого объекта.
     */
    protected void copyFrom(ConfigYAML other) {
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
