package ru.spliterash.musicbox.song;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.song.songContainers.types.SongContainer;
import ru.spliterash.musicbox.song.songContainers.SongContainerFactory;
import ru.spliterash.musicbox.song.songContainers.factory.FolderContainerFactory;
import ru.spliterash.musicbox.song.songContainers.factory.ListContainerFactory;
import ru.spliterash.musicbox.song.songContainers.factory.SingletonContainerFactory;

import java.io.File;
import java.util.*;

@SuppressWarnings("unused")
@UtilityClass

public class MusicBoxSongManager {
    public final String MASTER_CONTAINER = "MASTER";
    /**
     * NBT тег чтобы определить является ли пластинка кастомной
     */
    public final String NBT_NAME = "musicBoxSongHash";
    private final Set<SongContainerFactory<?>> factorySet = new HashSet<>();
    @Getter
    private final SongContainer masterContainer = new MasterContainer();
    // Для быстрого поиска
    @Getter
    private List<MusicBoxSong> allSongs;
    @Getter
    private MusicBoxSongContainer rootContainer;

    static {
        factorySet.add(new FolderContainerFactory());
        factorySet.add(new SingletonContainerFactory());
        factorySet.add(new ListContainerFactory());
    }

    public Optional<SongContainer> getContainerById(String str) {
        if (str.equals(MASTER_CONTAINER))
            return Optional.of(masterContainer);
        String[] split = str.split(":");
        if (split.length != 2)
            return Optional.empty();
        int id;
        try {
            id = Integer.parseInt(split[1]);
        } catch (Exception ex) {
            return Optional.empty();
        }
        return factorySet
                .stream()
                .filter(c -> c.getKey().equalsIgnoreCase(split[0]))
                .findFirst()
                .map(f -> f.parseContainer(id));

    }

    public void reload(File rootFolder) {
        rootContainer = new MusicBoxSongContainer(rootFolder, null, false);
        // ArrayList так как по нему быстрее искать элементы
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

    public Optional<MusicBoxSongContainer> findContainerById(int id) {
        return Optional.of(rootContainer.findById(id));
    }

    public boolean tryReplaceLegacyItem(Player player, ItemStack stack) {
        if (stack == null)
            return false;
        String musicBoxLegacyTagValue = NBTEditor.getString(stack, "musicbox");
        if (musicBoxLegacyTagValue == null)
            return false;
        MusicBoxSong song = findByName(musicBoxLegacyTagValue).orElse(null);
        if (song == null) {
            player.sendMessage(Lang.LEGACY_DISC_NOT_FOUND.toString("{song}", musicBoxLegacyTagValue));
        } else {
            player.getInventory().remove(stack);
            player.getInventory().addItem(song.getSongStack());
            player.sendMessage(Lang.LEGACY_DISC_REPLACE.toString());
        }
        return true;
    }

    // Ломбук не умеет на лету обрабатывать UtilityClass

    /**
     * Имплементация контейнера, содержащая всё что только есть
     */
    @SuppressWarnings("RedundantModifiersUtilityClassLombok")
    private static class MasterContainer implements SongContainer {

        @Override
        public String getNameId() {
            return MASTER_CONTAINER;
        }

        @Override
        public List<MusicBoxSong> getSongs() {
            ArrayList<MusicBoxSong> list = new ArrayList<>(allSongs);
            Collections.shuffle(list);
            return list;
        }

        @Override
        public List<MusicBoxSong> getSongsShuffle() {
            return getSongs();
        }
    }

}
