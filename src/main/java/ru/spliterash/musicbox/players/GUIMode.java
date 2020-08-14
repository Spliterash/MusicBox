package ru.spliterash.musicbox.players;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.minecraft.GUI;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongContainer;
import ru.spliterash.musicbox.utils.EconomyUtils;
import ru.spliterash.musicbox.utils.classes.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * Класс для больших кусков кода связанных с GUI
 */
@UtilityClass
public class GUIMode {


    public Pair<ItemStack, GUI.InventoryAction>[] DEFAULT_BAR;

    static {
        //noinspection unchecked
        DEFAULT_BAR = new Pair[7];
    }

    /**
     * Вызывается когда игрок хочет просто послушать музыку
     *
     * @param player       Сам игрок
     * @param musicBoxSong Музыка
     */
    public void playerPlayMusic(Player player, MusicBoxSong musicBoxSong) {
        PlayerWrapper
                .getInstance(player)
                .play(musicBoxSong);
    }

    public List<String> addContainerToPlaylist(MusicBoxSongContainer container) {
        return Lang.ADD_CONTAINER_TO_PLAYLIST.toList();
    }

    public void addToPlaylist(Player player, MusicBoxSong song) {
        //TODO
    }

    public List<String> addMusicToPlaylistLore(MusicBoxSong song) {
        return Lang.ADD_MUSIC_TO_PLAYLIST.toList();
    }

    public void addToPlaylist(Player player, MusicBoxSongContainer container) {
        //TODO
    }

    public List<String> playerBuyMusicLore(MusicBoxSong musicBoxSong) {
        return Lang.BUY_MUSIC_LORE.toList("{price}", String.valueOf(EconomyUtils.getDiscPrice()));
    }

    /**
     * Процесс покупки
     *
     * @param player       Игрок который покупает пластинку
     * @param musicBoxSong Покупаемая мелодия
     */
    public void playerBuyMusic(Player player, MusicBoxSong musicBoxSong) {
        double price = EconomyUtils.getDiscPrice();
        if (EconomyUtils.canBuy(player, price)) {
            HashMap<Integer, ItemStack> result = player.getInventory().addItem(musicBoxSong.getSongStack());
            if (result.size() > 0) {
                player.sendMessage(Lang.NO_INVENTORY_SPACE.toString());
            } else {
                EconomyUtils.buyNoMessage(player, price);
                player.sendMessage(Lang.DISC_BUYED.toString("{disc}", musicBoxSong.getName()));
            }
        }
    }

    public List<String> buyAllContainerLore(MusicBoxSongContainer container) {
        return Lang.BUY_CONTAINER_LORE.toList(
                "{price}",
                String.valueOf(EconomyUtils.getDiscPrice() * container.getAllSongs().size())
        );
    }

    public void buyAllContainer(Player player, MusicBoxSongContainer container) {
        for (MusicBoxSong song : container.getAllSongs()) {
            playerBuyMusic(player, song);
        }
    }
}
