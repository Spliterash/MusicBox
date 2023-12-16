package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

public class GiveSingleExecutor extends AbstractSelect {
    public GiveSingleExecutor() {
        super("musicbox.give");
    }

    @Override
    protected void noArgs(CommandSender sender, Player player) {
        GUIActions.openGiveInventorySingle(PlayerWrapper.getInstance(player));
    }

    @Override
    protected void processSong(CommandSender sender, Player target, MusicBoxSong song, String[] args) {
        // Who cares.......
        GUIActions.giveDisc(PlayerWrapper.getInstance(target), song);
    }
}
