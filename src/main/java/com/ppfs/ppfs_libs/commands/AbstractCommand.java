package com.ppfs.ppfs_libs.commands;

import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Абстрактный класс для создания команд, поддерживающих подкоманды и автодополнение.
 */
public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    /**
     * Карта зарегистрированных подкоманд, где ключ — имя команды, а значение — объект SubCommand.
     */
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    /**
     * Имя основной команды.
     */
    private final String name;

    public AbstractCommand(String name, Plugin plugin) {
        PluginCommand command = plugin.getServer().getPluginCommand(name);
        this.name = name;
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    /**
     * Регистрирует новую подкоманду.
     *
     * @param subCommand Объект подкоманды для регистрации.
     */
    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    /**
     * Метод для выполнения основной команды, если отсутствуют подкоманды.
     *
     * @param sender Отправитель команды (игрок или консоль).
     * @param cmd    Объект команды.
     * @param label  Лейбл команды (основное имя команды).
     * @param args   Аргументы команды.
     */
    public void execute(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args){}

    /**
     * Обрабатывает выполнение команды и делегирует вызовы подкомандам, если они существуют.
     *
     * @param sender  Отправитель команды.
     * @param command Объект команды.
     * @param s       Лейбл команды.
     * @param args    Аргументы команды.
     * @return true, если команда выполнена корректно.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0 && !subCommands.isEmpty()) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                // Проверяем разрешения отправителя для подкоманды
                if (!sender.hasPermission(getPermission(subCommand.getName()))) {
                    subCommand.noPermission(sender, command, s, args);
                    return true;
                }
                // Выполняем подкоманду
                subCommand.execute(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }
        execute(sender, command, s, args);
        return true;
    }

    /**
     * Формирует строку разрешения для подкоманды.
     *
     * @param subCommandName Имя подкоманды.
     * @return Строка разрешения для подкоманды (например, "command.subcommand").
     */
    private String getPermission(String subCommandName) {
        return name.toLowerCase() + "." + subCommandName.toLowerCase();
    }

    /**
     * Фильтрует список строк для автодополнения, оставляя только строки, начинающиеся с последнего аргумента.
     *
     * @param strings Список строк для фильтрации.
     * @param args    Аргументы команды.
     * @return Отфильтрованный список строк.
     */
    private List<String> filter(List<String> strings, String... args) {
        if (strings == null || strings.isEmpty()) return new ArrayList<>();
        String lastArg = args[args.length - 1].toLowerCase().trim();
        List<String> filtered = new ArrayList<>();
        for (String string : strings) {
            if (string.toLowerCase().startsWith(lastArg)) filtered.add(string);
        }
        return filtered;
    }

    /**
     * Предоставляет список возможных завершений команды для автодополнения.
     *
     * @param sender Отправитель команды.
     * @param args   Аргументы команды.
     * @return Список возможных завершений команды.
     */
    public List<String> complete(CommandSender sender, String... args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String key : subCommands.keySet()) {
                if (sender.hasPermission(getPermission(key))) {
                    completions.add(key);
                }
            }
            return completions;
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                return subCommand.complete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return Collections.emptyList();
    }

    /**
     * Реализует автодополнение команды, вызывая метод {@link #complete(CommandSender, String...)} и фильтруя результаты.
     *
     * @param commandSender Отправитель команды.
     * @param command       Объект команды.
     * @param s             Лейбл команды.
     * @param strings       Аргументы команды.
     * @return Список завершений для автодополнения.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return filter(complete(commandSender, strings), strings);
    }
}
