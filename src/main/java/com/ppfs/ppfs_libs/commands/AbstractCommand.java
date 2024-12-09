package com.ppfs.ppfs_libs.commands;

import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final String name;

    public AbstractCommand(String name, Plugin plugin) {
        PluginCommand command = plugin.getServer().getPluginCommand(name);
        this.name = name;
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    public abstract void execute(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0 && !subCommands.isEmpty()) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                if (!sender.hasPermission(getPermission(subCommand.getName()))) {
                    subCommand.noPermission(sender, command, s, args);
                    return true;
                }
                subCommand.execute(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }
        execute(sender, command, s, args);
        return true;
    }

    private String getPermission(String subCommandName) {
        return name.toLowerCase() + "." + subCommandName.toLowerCase();
    }

    private List<String> filter(List<String> strings, String... args) {
        if (strings == null || strings.isEmpty()) return new ArrayList<>();
        String lastArg = args[args.length - 1].toLowerCase().trim();
        List<String> filtered = new ArrayList<>();
        for (String string : strings) {
            if (string.toLowerCase().startsWith(lastArg)) filtered.add(string);
        }
        return filtered;
    }

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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return filter(complete(commandSender, strings), strings);
    }
}
