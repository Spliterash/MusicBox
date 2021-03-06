package ru.spliterash.musicbox.commands;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface SubCommand {
    void execute(Player player, String[] args);

    default String getPex() {
        return null;
    }

    default List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
