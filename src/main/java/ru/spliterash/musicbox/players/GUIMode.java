package ru.spliterash.musicbox.players;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongContainer;

import java.util.List;

/**
 * Класс для больших кусков кода
 * Связанных с GUI
 */
@UtilityClass
public class GUIMode {

    /**
     * Вызывается когда игрок хочет просто послушать музыку
     *
     * @param player       Сам игрок
     * @param musicBoxSong Музыка
     */
    public void playerPlayMusic(Player player, MusicBoxSong musicBoxSong) {
        PlayerWrapper instance = PlayerWrapper.getInstance(player);
        instance.play(musicBoxSong);
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

    public List<String> buyMusicLore(MusicBoxSong musicBoxSong) {
        return Lang.BUY_MUSIC_LORE.toList();
    }

    /**
     * Процесс покупки
     * @param player Игрок который покупает пластинку
     * @param musicBoxSong Покупаемая мелодия
     */
    public void playerBuyMusic(Player player, MusicBoxSong musicBoxSong) {

    }
}
