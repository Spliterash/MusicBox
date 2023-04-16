package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.StringUtils;

import java.util.List;
import java.util.stream.Stream;

public class SilentExecutor implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        PlayerWrapper wrapper = PlayerWrapper.getInstance(player);

        if (args.length == 0)
            wrapper.setSilent(!wrapper.isSilent());
        else if (args[0].equalsIgnoreCase("on"))
            wrapper.setSilent(true);
        else if (args[0].equalsIgnoreCase("off"))
            wrapper.setSilent(false);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return StringUtils.tabCompletePrepare(args, Stream.of("off", "on"));
    }
}
