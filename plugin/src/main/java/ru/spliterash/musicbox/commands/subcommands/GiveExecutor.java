package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

public class GiveExecutor extends AbstractSelect {
    public GiveExecutor() {
        super("musicbox.give");
    }
    @Override
    protected void noArgs(CommandSender sender, Player player) {
        GUIActions.openGetInventory(PlayerWrapper.getInstance(player));
    }
    @Override
    protected void processSong(CommandSender sender, Player target, MusicBoxSong song, String[] args) {
        GUIActions.giveDisc(PlayerWrapper.getInstance(target), song);
    }
}
