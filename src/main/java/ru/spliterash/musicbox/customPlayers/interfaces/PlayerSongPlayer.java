package ru.spliterash.musicbox.customPlayers.interfaces;

import ru.spliterash.musicbox.customPlayers.models.PlayerPlayerModel;

/**
 * Название идиотское, согласен
 * На русском - Проигрыватель звука игрока
 */
public interface PlayerSongPlayer extends MusicBoxSongPlayer {
    PlayerPlayerModel getModel();
}
