package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class SilentExecutor implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 2) {
            if (!sender.hasPermission("musicbox.admin")) {
                sender.sendMessage(Lang.NO_PEX.toString());
                return;
            }
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Lang.PLAYER_OFLLINE.toString("{player}", args[1]));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Lang.SPECIFY_PLAYER.toString());
            return;
        }
        PlayerWrapper wrapper = PlayerWrapper.getInstance(target);

        if (args.length == 0) {
            wrapper.setSilent(!wrapper.isSilent());
            return;
        }

        if (args[0].equalsIgnoreCase("on"))
            wrapper.setSilent(true);
        else if (args[0].equalsIgnoreCase("off"))
            wrapper.setSilent(false);
        else if (args[0].equalsIgnoreCase("switch"))
            wrapper.setSilent(!wrapper.isSilent());
        else {
            sender.sendMessage("Unknown type");
        }

        if (sender != target)
            sender.sendMessage(Lang.SILENT_MODE_RESPONSE.toString(
                    "{player}", target.getName(),
                    "{state}", wrapper.isSilent() ? Lang.ENABLE.toString() : Lang.DISABLE.toString())
            );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1)
            return StringUtils.tabCompletePrepare(args, Stream.of("off", "on"));
        else if (args.length <= 2)
            return StringUtils.tabCompletePrepare(
                    args,
                    1,
                    Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
            );
        else
            return Collections.emptyList();
    }
}
