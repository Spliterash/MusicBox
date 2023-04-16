package ru.spliterash.musicbox.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;

import java.util.Collections;
import java.util.List;

public interface SubCommand {
    default void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player)
            execute((Player) sender, args);
        else
            sender.sendMessage(Lang.ONLY_PLAYERS.toString());

    }

    default void execute(Player player, String[] args) {
    }

    default String getPex() {
        return null;
    }

    default List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
