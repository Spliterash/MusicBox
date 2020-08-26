package ru.spliterash.musicbox;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.spliterash.musicbox.commands.MusicBoxExecutor;
import ru.spliterash.musicbox.db.DatabaseLoader;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSongManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.jar.JarEntry;

@Getter
public final class MusicBox extends JavaPlugin {
    @Getter
    private static MusicBox instance;
    private MusicBoxConfig configObject;
    private boolean loaded = false;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultValues();
        //Поскольку нужно прогрузить базу
        //noinspection ResultOfMethodCallIgnored
        DatabaseLoader.getBase();
        registerCommand("musicbox", new MusicBoxExecutor());
        Bukkit.getPluginManager().registerEvents(new Handler(), this);
        Bukkit.getScheduler().runTaskAsynchronously(this, this::reloadPlugin);
    }

    public void sendMessage(String pex, String noPexMessage, String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission(pex))
                player.sendMessage(message);
            else
                player.sendMessage(noPexMessage);
        }
    }

    private void saveDefaultValues() {
        boolean firstRun = !getDataFolder().isDirectory();
        saveDefaultConfig();
        if (firstRun)
            saveMyMusic();
    }

    @SuppressWarnings("SameParameterValue")
    private void registerCommand(String command, TabExecutor executor) {
        PluginCommand cmd = getCommand(command);
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);
    }

    public void reloadPlugin() {
        loaded = false;
        try {
            configObject = MusicBoxConfig.parseConfig(new FileInputStream(new File(getDataFolder(), "config.yml")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Lang.reload(new File(getDataFolder(), "lang"), configObject.getLang());
        PlayerWrapper.clearAll();
        MusicBoxSongManager.reload(new File(getDataFolder(), "songs"));
        GUIActions.reloadGUI();
        loaded = true;
    }

    @Override
    public void onDisable() {
        PlayerWrapper.clearAll();
    }

    private void saveMyMusic() {
        try {
            java.util.jar.JarFile jar = new java.util.jar.JarFile(getFile());
            Enumeration<JarEntry> enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                JarEntry entry = enumEntries.nextElement();
                File realFile = new File(getDataFolder(), entry.getName());
                if (!entry.getName().startsWith("songs/"))
                    continue;
                if (entry.isDirectory()) { // if its a directory, create it
                    realFile.mkdir();
                    continue;
                }
                // get the input stream

                try (java.io.InputStream in = jar.getInputStream(entry); java.io.FileOutputStream out = new java.io.FileOutputStream(realFile)) {
                    byte[] buffer = new byte[4096];
                    int n;
                    while (-1 != (n = in.read(buffer))) {
                        out.write(buffer, 0, n);
                    }
                }
            }
            jar.close();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}
