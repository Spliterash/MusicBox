package ru.spliterash.musicbox.song;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.utils.FileUtils;
import ru.spliterash.musicbox.utils.JavaUtils;
import ru.spliterash.musicbox.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class MusicBoxSongContainer {
    private final MusicBoxSongContainer parent;
    private List<MusicBoxSongContainer> subContainers;
    private List<MusicBoxSong> songs;
    private final String name;
    private final List<String> lore;

    public MusicBoxSongContainer(File folder, MusicBoxSongContainer parent) {
        if (!folder.isDirectory()) {
            throw new RuntimeException("File is not folder");
        }
        this.parent = parent;
        name = StringUtils.t(folder.getName());
        File infoFile = new File(folder, "info.txt");
        if (infoFile.isFile()) {
            List<String> tempLore;
            try {
                tempLore = FileUtils.readFileToList(infoFile);

            } catch (IOException ex) {
                tempLore = JavaUtils.stackTraceToList(ex.getStackTrace());
            }
            lore = tempLore;
        } else {
            lore = null;
        }
        loadSongs(Objects.requireNonNull(folder.listFiles(f -> f.getName().endsWith(".nbs"))));
        loadSubContainers(Objects.requireNonNull(folder.listFiles(File::isDirectory)));
    }

    /**
     * Загружает папки
     */
    private void loadSubContainers(File[] folders) {
        ArrayList<MusicBoxSongContainer> subContainersTemp = new ArrayList<>(folders.length);
        for (File folder : folders) {
            MusicBoxSongContainer container = new MusicBoxSongContainer(folder, this);
            subContainersTemp.add(container);
        }
        subContainers = Collections.unmodifiableList(subContainersTemp);
    }

    /**
     * Загружает музыку из текущей папки
     */
    private void loadSongs(File[] songFiles) {
        ArrayList<MusicBoxSong> songsTemp = new ArrayList<>(songFiles.length);
        for (File file : songFiles) {
            try {
                MusicBoxSong song = new MusicBoxSong(file);
                songsTemp.add(song);
            } catch (SongNullException e) {
                MusicBox.getInstance().getLogger().warning("Can't load " + file);
            }
        }
        songs = Collections.unmodifiableList(songsTemp);
    }

    public List<MusicBoxSong> getAllSongs() {
        List<MusicBoxSong> list = new LinkedList<>(songs);
        for (MusicBoxSongContainer container : subContainers) {
            list.addAll(container.getAllSongs());
        }
        return list;
    }

    /**
     * Сгенерировать GUI для этого контейнера
     */
    public SongContainerGUI generateGUI(Player player) {
        return new SongContainerGUI(this, player);
    }

    public ItemStack getItemStack() {
        ItemStack chest = XMaterial.CHEST.parseItem();
        ItemMeta meta = chest.getItemMeta();
        meta.setDisplayName(Lang.FOLDER_FORMAT.toString("{folder}", getName()));
        meta.setLore(lore);
        chest.setItemMeta(meta);
        return chest;
    }
}
