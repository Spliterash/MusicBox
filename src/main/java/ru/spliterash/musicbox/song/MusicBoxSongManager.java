package ru.spliterash.musicbox.song;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class MusicBoxSongManager {
    /**
     * NBT тег чтобы определить является ли пластинка кастомной
     */
    public final String NBT_NAME = "musicBoxSongHash";
    // Для быстрого поиска
    @Getter
    private List<MusicBoxSong> allSongs;
    @Getter
    private MusicBoxSongContainer rootContainer;

    public void reload(File rootFolder) {
        rootContainer = new MusicBoxSongContainer(rootFolder, null, false);
        // ArrayList так как по нему быстрее искать переменные
        allSongs = Collections.unmodifiableList(new ArrayList<>(rootContainer.getAllSongs()));
    }

    public Optional<MusicBoxSong> findByName(String name) {
        return allSongs
                .stream()
                .filter(s -> s.getName().equals(name))
                .findFirst();
    }

    public Optional<MusicBoxSong> findSongByHash(int hash) {
        return allSongs
                .stream()
                .filter(s -> s.getHash() == hash)
                .findFirst();
    }

    public Optional<MusicBoxSong> findByItem(ItemStack stack) {
        if (stack == null)
            return Optional.empty();
        int hash = NBTEditor.getInt(stack, NBT_NAME);
        if (hash != 0)
            return findSongByHash(hash);
        else
            return Optional.empty();
    }

}
