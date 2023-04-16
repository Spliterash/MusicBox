package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.MusicBoxExecutor;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.players.PlayerWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShutUp implements SubCommand {
    private final MusicBoxExecutor parent;

    public ShutUp(MusicBoxExecutor parent) {
        this.parent = parent;
    }

    @Override
    public void execute(CommandSender player, String[] args) {
        if (args.length == 0) {
            parent.sendHelp(player);
            return;
        }
        shutUp(player, args);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length <= 1) {
            //noinspection ArraysAsListWithZeroOrOneArgument
            return Arrays.asList("shutup");
        } else if (args[0].equalsIgnoreCase("shutup")) {
            String startWith = args[1];
            return Bukkit
                    .getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName)
                    .filter(p -> p.startsWith(startWith))
                    .collect(Collectors.toList());
        }
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

    @Override
    public String getPex() {
        return "musicbox.shutup";
    }
}
