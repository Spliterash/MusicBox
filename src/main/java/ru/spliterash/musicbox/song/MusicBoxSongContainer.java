package ru.spliterash.musicbox.song;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.gui.song.SongContainerGUI;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.songContainers.factory.FolderContainerFactory;
import ru.spliterash.musicbox.song.songContainers.types.FullSongContainer;
import ru.spliterash.musicbox.song.songContainers.types.SubSongContainer;
import ru.spliterash.musicbox.utils.FileUtils;
import ru.spliterash.musicbox.utils.JavaUtils;
import ru.spliterash.musicbox.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@Getter
public class MusicBoxSongContainer implements SubSongContainer, FullSongContainer {
    private final MusicBoxSongContainer parent;
    private List<MusicBoxSongContainer> subContainers;
    private List<MusicBoxSong> songs;
    private final String name;
    private final List<String> lore;
    private final int hash;

    public MusicBoxSongContainer(File folder, MusicBoxSongContainer parent) {
        this(folder, parent, true);
    }

    public MusicBoxSongContainer(File folder, MusicBoxSongContainer parent, boolean keepFolderName) {
        if (!folder.isDirectory()) {
            throw new RuntimeException("File is not folder");
        }
        this.parent = parent;
        if (keepFolderName)
            name = StringUtils.t(folder.getName());
        else
            name = "";
        File infoFile = new File(folder, "info.txt");
        if (infoFile.isFile()) {
            List<String> tempLore;
            try {
                tempLore = FileUtils.readFileToList(infoFile);

            } catch (IOException ex) {
                tempLore = JavaUtils.stackTraceToList(ex.getStackTrace());
            }
            lore = Collections.unmodifiableList(StringUtils.t(tempLore));
        } else {
            lore = Collections.emptyList();
        }
        hash = folder.getPath().hashCode();
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
        subContainersTemp.sort(Comparator.comparing(container -> ChatColor.stripColor(container.getName())));

        subContainers = Collections.unmodifiableList(subContainersTemp);
    }

    /**
     * Загружает музыку из текущей папки
     */
    private void loadSongs(File[] songFiles) {
        ArrayList<MusicBoxSong> songsTemp = new ArrayList<>(songFiles.length);
        for (File file : songFiles) {
            try {
                MusicBoxSong song = new MusicBoxSong(file, this);
                songsTemp.add(song);
            } catch (SongNullException e) {
                MusicBox.getInstance().getLogger().warning("Can't load " + file);
            }
        }

        songsTemp.sort(Comparator.comparing(song -> ChatColor.stripColor(song.getName())));

        songs = Collections.unmodifiableList(songsTemp);
    }

    /**
     * Создать GUI для этого контейнера
     */
    public SongContainerGUI createGUI(PlayerWrapper player) {
        return new SongContainerGUI(this, player);
    }

    public ItemStack getItemStack() {
        return getItemStack(Collections.emptyList());
    }

    public ItemStack getItemStack(List<String> extraLines) {
        ItemStack chest = XMaterial.CHEST.parseItem();
        ItemMeta meta = chest.getItemMeta();
        meta.setDisplayName(Lang.FOLDER_FORMAT.toString("{folder}", getName()));
        List<String> tempLore;
        if (extraLines.size() > 0) {
            tempLore = new ArrayList<>(lore);
            tempLore.addAll(extraLines);
        } else
            tempLore = lore;
        meta.setLore(tempLore);
        chest.setItemMeta(meta);
        return chest;
    }

    public MusicBoxSongContainer findById(int id) {
        if (getHash() == id)
            return this;
        for (MusicBoxSongContainer container : subContainers) {
            MusicBoxSongContainer c = container.findById(id);
            if (c != null)
                return c;
        }
        return null;
    }

    @Override
    public String getNameId() {
        return FolderContainerFactory.NAME + ":" + getHash();
    }

    @Override
    public List<MusicBoxSongContainer> getSubContainers() {
        return subContainers;
    }

    @Override
    public SubSongContainer getParentContainer() {
        return getParent();
    }
}
