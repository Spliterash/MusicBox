package ru.spliterash.musicbox.customPlayers.interfaces;

import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.List;

public interface IPlayList {
    MusicBoxSong getNext();

    List<MusicBoxSong> getNextFiveSong();

    boolean hasNext();
}
