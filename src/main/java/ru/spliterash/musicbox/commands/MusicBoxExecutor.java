package ru.spliterash.musicbox.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.commands.subcommands.GetExecutor;
import ru.spliterash.musicbox.commands.subcommands.PlayExecutor;
import ru.spliterash.musicbox.commands.subcommands.PlaylistExecutor;
import ru.spliterash.musicbox.commands.subcommands.ShopExecutor;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.ArrayUtils;

import java.util.*;

public class MusicBoxExecutor implements TabExecutor {
    private final Map<String, SubCommand> subs = new HashMap<>();

    public MusicBoxExecutor() {
        subs.put("shop", new ShopExecutor());
        subs.put("get", new GetExecutor());
        subs.put("playlist", new PlaylistExecutor(this));
        subs.put("play", new PlayExecutor(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MusicBox.getInstance().isLoaded()) {
            sender.sendMessage(ChatColor.RED + "Plugin not loaded, please wait or check console if wait too long");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.ONLY_PLAYERS.toString());
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("musicbox.use")) {
            player.sendMessage(Lang.NO_PEX.toString());
            return true;
        }
        if (args.length == 0) {
            PlayerWrapper.getInstance(player).openDefaultInventory();
            return true;
        }
        SubCommand executor = subs.get(args[0]);
        if (executor == null) {
            sendHelp(player);
        } else if (executor.getPex() == null || player.hasPermission(executor.getPex())) {
            executor.execute(player, ArrayUtils.removeFirst(String.class, args));
        } else {
            player.sendMessage(Lang.NO_PEX.toString());
        }
        return true;
    }

    public void sendHelp(Player player) {
        player.sendMessage(Lang.COMMAND_HELP.toArray());
        if (player.hasPermission("musicbox.shop")) {
            player.sendMessage(Lang.COMMAND_HELP_SHOP.toString());
        }
        if (player.hasPermission("musicbox.get")) {
            player.sendMessage(Lang.COMMAND_HELP_GET.toString());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player) sender;
        if (args.length <= 1) {
            List<String> tabComplete = new LinkedList<>();
            if (player.hasPermission("musicbox.use")) {
                tabComplete.add("play");
                tabComplete.add("playlist");
            }
            if (player.hasPermission("musicbox.shop"))
                tabComplete.add("shop");
            if (player.hasPermission("musicbox.get"))
                tabComplete.add("get");
            return tabComplete;
        } else {
            SubCommand executor = subs.get(args[0].toLowerCase());
            if (executor != null) {
                if (executor.getPex() == null || player.hasPermission(executor.getPex()))
                    return executor.tabComplete(player, ArrayUtils.removeFirst(String.class, args));
            }
        }
        return Collections.emptyList();
    }
}
