package ru.spliterash.musicbox;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import ru.spliterash.musicbox.commands.MusicBoxExecutor;
import ru.spliterash.musicbox.players.PlayerInstance;
import ru.spliterash.musicbox.song.MusicBoxSongManager;

import java.io.File;

public final class MusicBox extends JavaPlugin {
    @Getter
    private static MusicBox instance;
    private MusicBoxConfig config;
    private Yaml yaml = new Yaml();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        registerCommand("musicbox", new MusicBoxExecutor());
        Bukkit.getPluginManager().registerEvents(new Handler(), this);
        reloadPlugin();
    }

    @SuppressWarnings("SameParameterValue")
    private void registerCommand(String command, TabExecutor executor) {
        PluginCommand cmd = getCommand(command);
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);
    }

    public void reloadPlugin() {
        PlayerInstance.clearAll();

        MusicBoxSongManager.reload(new File(getDataFolder(), "songs"));
        config = yaml.loadAs(getResource("config.yml"), MusicBoxConfig.class);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public MusicBoxConfig getConfigObject() {
        return config;
    }
}
