package ru.spliterash.musicbox.customPlayers.interfaces;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
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
    MusicBoxSong getMusicBoxSong();

    /**
     * Уничтожает проигрыватель
     * Чтобы лишний раз не кастовать
     * Так то любой SongPlayer имеет этот метод
     */
    void destroy();


    /**
     * Для сокращения кода
     */
    default SongPlayer getSongPlayer() {
        return (SongPlayer) this;
    }

    /**
     * Получить тик который играет в данный момент
     */
    short getTick();
}
