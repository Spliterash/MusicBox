package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShopExecutor extends AbstractGet {
    @Override
    protected void noArgs(Player player) {
        PlayerWrapper
                .getInstance(player)
                .openShopInventory();
    }

    @Override
    protected void processSong(Player player, MusicBoxSong song) {
        GUIActions.playerBuyMusic(PlayerWrapper.getInstance(player), song);
    }
}
