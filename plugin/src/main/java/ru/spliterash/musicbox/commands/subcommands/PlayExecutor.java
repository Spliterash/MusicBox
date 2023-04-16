package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.commands.MusicBoxExecutor;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

public class PlayExecutor extends AbstractSelect {
    private final MusicBoxExecutor parent;

    public PlayExecutor(MusicBoxExecutor parent) {
        super("musicbox.use");
        this.parent = parent;
    }

    @Override
    protected void noArgs(Player player) {
        parent.sendHelp(player);
    }

    @Override
    protected void processSong(Player player, MusicBoxSong song) {
        PlayerWrapper.getInstance(player).play(song);
    }
}
