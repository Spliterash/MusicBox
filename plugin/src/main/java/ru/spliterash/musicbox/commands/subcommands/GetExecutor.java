package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

public class GetExecutor extends AbstractSelect {
    public GetExecutor() {
        super("musicbox.get");
    }

    @Override
    protected void noArgs(Player player) {
        GUIActions.openGetInventory(PlayerWrapper.getInstance(player));
    }

    @Override
    protected void processSong(Player player, MusicBoxSong song) {
        GUIActions.giveDisc(PlayerWrapper.getInstance(player), song);
    }
}
