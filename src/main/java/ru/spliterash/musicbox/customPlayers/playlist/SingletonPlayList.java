package ru.spliterash.musicbox.customPlayers.playlist;

import lombok.RequiredArgsConstructor;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.song.MusicBoxSong;

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
}
