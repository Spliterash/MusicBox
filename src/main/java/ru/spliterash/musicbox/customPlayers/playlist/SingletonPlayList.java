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
    public MusicBoxSong getNext() {
        if (giveSong) {
            giveSong = false;
            return song;
        } else
            return null;
    }

    @Override
    public List<MusicBoxSong> getNextFiveSong() {
        if (giveSong)
            return Collections.singletonList(song);
        else
            return Collections.emptyList();

    }

    @Override
    public boolean hasNext() {
        return giveSong;
    }
}
