package ru.spliterash.musicbox.customPlayers.interfaces;

import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.List;

public interface IPlayList {

    MusicBoxSong getNext();

    List<MusicBoxSong> getNextSongs(int count);

    /**
     * Возращает следующий трек без увеличения счётчика
     */
    @Nullable
    default MusicBoxSong silentGetNext() {
        List<MusicBoxSong> next = getNextSongs(1);
        if (next.size() == 1)
            return next.get(0);
        else
            return null;
    }

    default boolean hasNext() {
        return getNextSongs(1).size() == 1;
    }

    List<MusicBoxSong> getPreviousSong(int count);

    MusicBoxSong getCurrent();

    void back(int count);

    int getSongNum(MusicBoxSong song);
}
