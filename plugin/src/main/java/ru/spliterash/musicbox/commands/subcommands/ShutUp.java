package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.MusicBoxExecutor;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.StringUtils;

import java.util.Collections;
import java.util.List;

public class ShutUp implements SubCommand {
    private final MusicBoxExecutor parent;

    public ShutUp(MusicBoxExecutor parent) {
        this.parent = parent;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            parent.sendHelp(sender);
            return;
        }

        shutUp(sender, args);
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender.hasPermission("musicbox.shutup");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return StringUtils.tabCompletePrepare(args, Bukkit
                    .getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName));
        } else
            return Collections.emptyList();
    }

    public void shutUp(CommandSender sender, String[] args) {
        Player p = Bukkit.getPlayer(args[0]);
        if (p == null) {
            sender.sendMessage(Lang.PLAYER_OFLLINE.toString("{player}", args[0]));
            return;
        }
        PlayerWrapper
                .getInstanceOptional(p)
                .ifPresent(PlayerWrapper::destroyActivePlayer);

        sender.sendMessage(Lang.SHUT_UPPED.toString("{player}", p.getName()));

    }
}
