package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.Bukkit;
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

public class AdminExecutor implements SubCommand {
    private final MusicBoxExecutor parent;

    public AdminExecutor(MusicBoxExecutor parent) {
        this.parent = parent;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            parent.sendHelp(player);
            return;
        }
        //noinspection SwitchStatementWithTooFewBranches
        switch (args[0]) {
            case "shutup":
                if (args.length == 2) {
                    shutUp(player, args);
                } else {
                    parent.sendHelp(player);
                    return;
                }
                break;
        }

    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length <= 1) {
            //noinspection ArraysAsListWithZeroOrOneArgument
            return Arrays.asList("shutup");
        } else if (args[0].toLowerCase().equals("shutup")) {
            String startWith = args[1];
            return Bukkit
                    .getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName)
                    .filter(p->p.startsWith(startWith))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void shutUp(Player player, String[] args) {
        Player p = Bukkit.getPlayer(args[1]);
        if (p == null) {
            player.sendMessage(Lang.PLAYER_OFLLINE.toString("{player}", args[1]));
            return;
        }
        PlayerWrapper
                .getInstanceOptional(player)
                .ifPresent(PlayerWrapper::destroyActivePlayer);
        player.sendMessage(Lang.SHUT_UPPED.toString("{player}", player.getName()));

    }

    @Override
    public String getPex() {
        return "musicbox.admin";
    }
}
