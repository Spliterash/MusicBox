package ru.spliterash.musicbox.customPlayers.interfaces;

import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.List;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "unused"})
public interface IPlayList {
    /**
     * Перемещает счётчик вперёд
     */
    void next();

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
    List<MusicBoxSong> getPrevSongs(int count);

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


    default boolean tryNext() {
        if (hasNext()) {
            next();
            return true;
        } else
            return false;
    }

    /**
     * Перемотать к этой музыке (если есть)
     *
     * @param song Собственно к чему надо перематывать
     */
    void setSong(MusicBoxSong song);
}
