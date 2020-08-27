package ru.spliterash.musicbox.customPlayers.playlist;

import lombok.RequiredArgsConstructor;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.Collections;
import java.util.List;

/**
 * Небольшая имплементация плейлиста
 * Содерижит только одну музыку и то, отдаст её только один раз
 */
@RequiredArgsConstructor
public class SingletonPlayList implements IPlayList {
    private final MusicBoxSong song;
    private boolean giveSong = true;

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public List<MusicBoxSong> getNextSongs(int a) {
        if (giveSong)
            return Collections.singletonList(song);
        else
            return Collections.emptyList();

    }

    @Override
    public boolean hasNext() {
        return giveSong;
    }

    @Override
    public List<MusicBoxSong> getPreviousSong(int count) {
        return Collections.emptyList();
    }

    @Override
    public MusicBoxSong getCurrent() {
        return song;
    }

    @Override
    public void back(int count) {
        if (count > 0)
            giveSong = true;
    }

    @Override
    public int getSongNum(MusicBoxSong song) {
        return 0;
    }
}
