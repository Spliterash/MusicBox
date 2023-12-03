package ru.spliterash.musicbox.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public interface SubCommand {
    void execute(CommandSender sender, String[] args);

    default boolean canExecute(CommandSender sender) {
        return sender.hasPermission("musicbox.use");
    }

    default List<String> tabComplete(CommandSender player, String[] args) {
        return Collections.emptyList();
    }
}
