package ru.spliterash.musicbox.customPlayers.interfaces;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import ru.spliterash.musicbox.song.MusicBoxSong;

/**
 * Все звуки созданные этим плагином
 */
public interface MusicBoxSongPlayer {
    /**
     * Получить музыку которая сейчас играет
     */
    MusicBoxSong getMusicBoxSong();

    /**
     * Чтобы лишний раз не кастовать
     * Так то любой SongPlayer имеет этот метод
     */
    void destroy();

    /**
     * Опять же, чтобы не кастовать
     * Устанавливает громкость от 0 до 100
     */
    void setVolume(byte volume);

    /**
     * Для сокращения кода
     */
    default SongPlayer getSongPlayer() {
        return (SongPlayer) this;
    }

}
