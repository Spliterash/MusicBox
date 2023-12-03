package ru.spliterash.musicbox.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.commands.subcommands.*;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MusicBoxExecutor implements TabExecutor {
    private final Map<String, SubCommand> subs = new HashMap<>();

    public MusicBoxExecutor() {
        GiveExecutor giveExecutor = new GiveExecutor();

        subs.put("shop", new ShopExecutor());
        subs.put("get", giveExecutor); // backward compatibility
        subs.put("give", giveExecutor);
        subs.put("playlist", new PlaylistExecutor());
        subs.put("play", new PlayExecutor());
        subs.put("shutup", new ShutUp(this));
        subs.put("reload", new ReloadExecutor());
        subs.put("silent", new SilentExecutor());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MusicBox.getInstance().isLoaded()) {
            sender.sendMessage(ChatColor.RED + "Plugin not loaded, please wait or check console if wait too long");
            return true;
        }

        if (!sender.hasPermission("musicbox.use")) {
            sender.sendMessage(Lang.NO_PEX.toString());
            return true;
        }

        if (args.length == 0) {
            if (sender instanceof Player)
                GUIActions.openDefaultInventory(PlayerWrapper.getInstance((Player) sender));
            else
                sendHelp(sender);
            return true;
        }
        SubCommand executor = subs.get(args[0]);
        if (executor == null) {
            sendHelp(sender);
            return true;
        }
        if (executor.canExecute(sender)) {
            executor.execute(sender, ArrayUtils.removeFirst(String.class, args));
        } else {
            sender.sendMessage(Lang.NO_PEX.toString());
        }
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(Lang.COMMAND_HELP.toArray());
        if (sender.hasPermission("musicbox.shop")) {
            sender.sendMessage(Lang.COMMAND_HELP_SHOP.toString());
        }
        if (sender.hasPermission("musicbox.give")) {
            sender.sendMessage(Lang.COMMAND_HELP_GIVE.toString());
        }
        if (sender.hasPermission("musicbox.admin")) {
            sender.sendMessage(Lang.ADMIN_HELP.toArray());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            List<String> tabComplete = new LinkedList<>();
            if (sender.hasPermission("musicbox.use")) {
                tabComplete.add("play");
                tabComplete.add("silent");
                if (sender instanceof Player)
                    tabComplete.add("playlist");
            }
            if (sender.hasPermission("musicbox.shop"))
                tabComplete.add("shop");
            if (sender.hasPermission("musicbox.give"))
                tabComplete.add("give");
            if (sender.hasPermission("musicbox.admin")) {
                tabComplete.add("shutup");
                tabComplete.add("reload");
            }
            if (args.length == 1)
                return tabComplete
                        .stream()
                        .filter(s -> s.startsWith(args[0]))
                        .collect(Collectors.toList());
            else
                return tabComplete;
        } else {
            SubCommand executor = subs.get(args[0].toLowerCase());
            if (executor != null) {
                if (executor.canExecute(sender))
                    return executor.tabComplete(sender, ArrayUtils.removeFirst(String.class, args));
            }
        }
        return Collections.emptyList();
    }
}
