package ru.spliterash.musicbox.customPlayers.interfaces;

import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.List;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "unused"})
public interface IPlayList {
    /**
     * Перемещает счётчик вперёд
     *
     * @return Переместился ли счётчик
     */
    boolean next();

    /**
     * Список следуюших song'ов
     *
     * @param count Количество
     */
    List<MusicBoxSong> getNextSongs(int count);

    /**
     * Список предыдуших song'ов
     *
     * @param count Количество
     */
    List<MusicBoxSong> getPrevSong(int count);

    /**
     * Есть ли следующая песня
     */
    boolean hasNext();

    /**
     * Есть ли предыдущая песня
     */
    boolean hasPrev();

    default boolean isSingleList() {
        return !hasNext() && !hasPrev();
    }

    MusicBoxSong getCurrent();

    void back(int count);

    int getSongNum(MusicBoxSong song);


}
