package ru.spliterash.musicbox;

import org.bukkit.plugin.java.JavaPlugin;
import ru.spliterash.musicbox.song.SongManager;

public final class MusicBox extends JavaPlugin {

    @Override
    public void onEnable() {
        SongManager.reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
