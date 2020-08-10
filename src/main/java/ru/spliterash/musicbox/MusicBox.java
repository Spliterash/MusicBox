package ru.spliterash.musicbox;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.spliterash.musicbox.song.SongManager;

import java.io.File;

public final class MusicBox extends JavaPlugin {
    @Getter
    private static MusicBox instance;


    @Override
    public void onEnable() {
        instance = this;
        SongManager.reload(new File(getDataFolder(), "songs"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
