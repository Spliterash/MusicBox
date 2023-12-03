package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.StringUtils;

public class PlaylistExecutor implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.ONLY_PLAYERS.toString());
            return;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            GUIActions.openPlaylistListEditor(PlayerWrapper.getInstance(player));
            return;
        }
        createPlaylist(player, args);
    }

    private void createPlaylist(Player player, String[] args) {
        if (args.length <= 0) {
            player.sendMessage(Lang.INPUT_NAME.toString());
            return;
        }
        String name = StringUtils.t(String.join(" ", args));
        GUIActions.openPlaylistEditor(
                PlayerWrapper.getInstance(player),
                new PlayerPlayListModel(-1, player.getUniqueId(), name)
        );
    }
}
