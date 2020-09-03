package ru.spliterash.musicbox.customPlayers.interfaces;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import ru.spliterash.musicbox.customPlayers.models.MusicBoxSongPlayerModel;
import ru.spliterash.musicbox.gui.song.SPControlGUI;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.Set;
import java.util.UUID;

/**
 * Все проигрыватели созданные этим плагином
 */
public interface MusicBoxSongPlayer {

    default SPControlGUI getControl() {
        return getMusicBoxModel().getControlGUI();
    }

    /**
     * Получить музыку которая сейчас играет
     */
    default MusicBoxSong getMusicBoxSong() {
        return getMusicBoxModel().getCurrentSong();
    }

    MusicBoxSongPlayerModel getMusicBoxModel();

    /**
     * Для сокращения кода
     */
    default SongPlayer getApiPlayer() {
        return (SongPlayer) this;
    }

    /**
     * Получить тик который играет в данный момент
     */
    short getTick();

    default IPlayList getPlayList() {
        return getMusicBoxModel().getPlayList();
    }

    boolean isDestroyed();

    default Set<UUID> getPlayers() {
        return getApiPlayer().getPlayerUUIDs();
    }

    /**
     * Вызывается event'ом когда музыка реально закончилась
     */
    default void onSongEnd() {
        getMusicBoxModel().pingSongEnded();
        getMusicBoxModel().onSongEnd();
    }

    /**
     * Просто сокращение
     * Полностью убивает SongPlayer
     */
    default void destroy() {
        getApiPlayer().destroy();
    }
}
