package ru.spliterash.musicbox.commands;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface SubCommand {
    public void execute(Player player, String[] args);

    default List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
