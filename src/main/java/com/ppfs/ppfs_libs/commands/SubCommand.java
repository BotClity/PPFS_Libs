package com.ppfs.ppfs_libs.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class SubCommand {
    private final Map<String, SubCommand> nestedSubCommands = new HashMap<>();
    @Getter
    private final String name;
    @Setter
    private boolean onlyPlayers = true;

    public SubCommand(String name) {
        this.name = name;
    }

    public void registerSubCommand(SubCommand subCommand) {
        nestedSubCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (onlyPlayers && !(sender instanceof Player)) {
            handle(sender, command, label, args);
            return;
        }

        if (args.length == 0) {
            handle(sender, command, label, args);
            return;
        }

        SubCommand subCommand = nestedSubCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            subCommand.execute(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        } else {
            handle(sender, command, label, args);
        }
    }

    public List<String> complete(CommandSender sender, String... args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String key : nestedSubCommands.keySet()) {
                if (sender.hasPermission(name.toLowerCase() + "." + key.toLowerCase())) {
                    completions.add(key);
                }
            }
            return completions;
        }
        SubCommand subCommand = nestedSubCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            return subCommand.complete(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return Collections.emptyList();
    }

    public void handle(CommandSender sender, Command command, String label, String... args) {
        sender.sendMessage("This command has no specific action.");
    }

    public void noPermission(CommandSender sender, Command command, String label, String... args){
        sender.sendMessage("No permissions");
    }

    public void onlyPlayerExecute(CommandSender sender, Command command, String label, String... args){
        sender.sendMessage("This command can only be executed by players.");
    }
}
