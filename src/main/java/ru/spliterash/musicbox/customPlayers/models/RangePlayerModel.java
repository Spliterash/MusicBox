package ru.spliterash.musicbox.customPlayers.models;

import lombok.Getter;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;

@Getter
public class RangePlayerModel {
    private final MusicBoxSongPlayerModel musicBoxModel;

    public RangePlayerModel(MusicBoxSongPlayerModel musicBoxModel) {
        this.musicBoxModel = musicBoxModel;
    }

    public void updateListeners() {

    }

    public PositionPlayer getSongPlayer() {
        return (PositionPlayer) musicBoxModel.getMusicBoxSongPlayer();
    }

    public void destroy() {
        // TODO
    }
}
