package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.commands.SubCommand;

public class ReloadExecutor implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(MusicBox.getInstance(), () -> {
            MusicBox.getInstance().reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "Reloaded");
        });

    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender.hasPermission("musicbox.admin");
    }
}
