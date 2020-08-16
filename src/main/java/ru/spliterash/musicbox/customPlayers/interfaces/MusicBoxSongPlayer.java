package ru.spliterash.musicbox.customPlayers.interfaces;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import ru.spliterash.musicbox.customPlayers.models.AllPlayerModel;
import ru.spliterash.musicbox.gui.RewindGUI;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * Все проигрыватели созданные этим плагином
 */
public interface MusicBoxSongPlayer {

    Map<MusicBoxSongPlayer, RewindGUI> rewindMap = new WeakHashMap<>();

    default RewindGUI getRewind() {
        return rewindMap.computeIfAbsent(this, RewindGUI::new);
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

    AllPlayerModel getMusicBoxModel();

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
}
