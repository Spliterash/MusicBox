package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

public class ShopExecutor extends AbstractSelect {
    public ShopExecutor() {
        super("musicbox.shop");
    }

    @Override
    protected void noArgs(Player player) {
        GUIActions.openShopInventory(PlayerWrapper.getInstance(player));
    }

    @Override
    protected void processSong(Player player, MusicBoxSong song) {
        GUIActions.playerBuyMusic(PlayerWrapper.getInstance(player), song);
    }
}
