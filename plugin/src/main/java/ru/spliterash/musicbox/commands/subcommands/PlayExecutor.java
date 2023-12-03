package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.commands.MusicBoxExecutor;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

public class PlayExecutor extends AbstractSelect {
    public PlayExecutor() {
        super("musicbox.use");
    }

    @Override
    protected void noArgs(CommandSender sender, Player player) {
        GUIActions.openDefaultInventory(PlayerWrapper.getInstance(player));
    }

    @Override
    protected void processSong(CommandSender sender, Player target, MusicBoxSong song, String[] args) {
        PlayerWrapper.getInstance(target).play(song);
    }
}
