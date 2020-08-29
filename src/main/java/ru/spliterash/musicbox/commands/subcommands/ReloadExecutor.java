package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.commands.SubCommand;

public class ReloadExecutor implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(MusicBox.getInstance(), () -> {
            MusicBox.getInstance().reloadPlugin();
            player.sendMessage(ChatColor.GREEN + "Reloaded");
        });

    }

    @Override
    public String getPex() {
        return "musicbox.admin";
    }
}
