package ru.spliterash.musicbox.customPlayers.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface PositionPlayer extends MusicBoxSongPlayer {
    Location getLocation();

    int getRange();

    void setRange(int range);

    default void addPlayer(Player player) {
        getApiPlayer().addPlayer(player);
    }

    default void removePlayer(Player player) {
        getApiPlayer().removePlayer(player);
    }
}
