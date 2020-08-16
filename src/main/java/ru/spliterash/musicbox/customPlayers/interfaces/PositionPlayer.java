package ru.spliterash.musicbox.customPlayers.interfaces;

import org.bukkit.Location;

public interface PositionPlayer extends MusicBoxSongPlayer{
    Location getLocation();
    void setLocation(Location location);
    int getRange();
    void setRange(int range);
}
