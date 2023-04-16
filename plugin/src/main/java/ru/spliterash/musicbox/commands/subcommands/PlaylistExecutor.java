package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.MusicBoxExecutor;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistExecutor implements SubCommand {
    private final MusicBoxExecutor parent;

    public PlaylistExecutor(MusicBoxExecutor parent) {
        this.parent = parent;
    }

    @Override
    public void execute(Player player, String[] args) {
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

    @Override
    public String getPex() {
        return "musicbox.use";
    }
}
