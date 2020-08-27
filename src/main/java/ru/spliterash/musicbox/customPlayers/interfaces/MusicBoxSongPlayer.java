package ru.spliterash.musicbox.customPlayers.interfaces;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import ru.spliterash.musicbox.customPlayers.models.MusicBoxSongPlayerModel;
import ru.spliterash.musicbox.gui.song.RewindGUI;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

/**
 * Все проигрыватели созданные этим плагином
 */
public interface MusicBoxSongPlayer {

    default RewindGUI getRewind() {
        return getMusicBoxModel().getRewind();
    }

    /**
     * Получить музыку которая сейчас играет
     */
    default MusicBoxSong getMusicBoxSong() {
        return getMusicBoxModel().getCurrentSong();
    }

    /**
     * Полное уничтожение
     * Не запускает следующую музыку если вызвать
     */
    default void totalDestroy() {
        getMusicBoxModel().totalDestroy();
    }

    /**
     * Уничтожает проигрыватель
     * Чтобы лишний раз не кастовать
     * Так то любой SongPlayer имеет этот метод
     */
    void destroy();

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
}
