package ru.spliterash.musicbox.customPlayers.models;

import lombok.Getter;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;
import ru.spliterash.musicbox.song.MusicBoxSong;

@Getter
public class PositionPlayerModel {
    private final PositionPlayer positionPlayer;
    private final MusicBoxSong song;

    public PositionPlayerModel(PositionPlayer positionPlayer, MusicBoxSong song) {
        this.positionPlayer = positionPlayer;
        this.song = song;
    }
}
