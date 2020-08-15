package ru.spliterash.musicbox.song;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Objects;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.spliterash.musicbox.utils.*;
import ru.spliterash.musicbox.Lang;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
public class MusicBoxSong {

    private final File file;
    private final String name;
    private final HashMap<String, String> hoverMap;
    private final MusicBoxSongContainer container;
    private final short length;
    private final float speed;
    private WeakReference<Song> songReference;
    private boolean newInstruments = false;

    MusicBoxSong(File songFile, MusicBoxSongContainer container) {
        this.file = songFile;
        this.container = container;
        Song song = getSong();
        this.newInstruments = SongUtils.containsNewInstrument(song);
        this.name = StringUtils.t(StringUtils.getOrEmpty(song.getTitle(), () -> FileUtils.getFilename(file.getName())));
        this.hoverMap = new HashMap<>();
        this.length = song.getLength();
        this.speed = song.getSpeed();
        String time = StringUtils.toHumanTime(getDuration());
        hoverMap.put("{length}", time);
        hoverMap.put("{author}", song.getAuthor());
        hoverMap.put("{original_author}", song.getOriginalAuthor());
    }

    public int getDuration() {
        return (int) Math.floor(length / speed);
    }

    public ItemStack getSongStack(XMaterial material) {
        return getSongStack(material, Collections.emptyList(), false);
    }

    /**
     * @param material Какой материал использовать
     * @return Айтем с этим материалом
     */
    public ItemStack getSongStack(XMaterial material, List<String> extraLines, boolean glow) {
        ItemStack stack = material.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Lang.SONG_NAME.toString("{song}", getName()));
        List<String> list = ArrayUtils.replaceOrRemove(Lang.SONG_LORE.toList(), hoverMap);
        if (newInstruments) {
            list.add(Lang.NEW_INSTRUMENT.toString());
        }
        list.addAll(extraLines);
        meta.setLore(list);
        stack.setItemMeta(meta);
        stack = NBTEditor.set(stack, getHash(), MusicBoxSongManager.NBT_NAME);
        if (glow)
            stack = ItemUtils.glow(stack);
        return stack;
    }

    /**
     * Смотрит есть ли Song в референсе
     * И если нету, то отдаёт загруженный с диска файл
     */
    public Song getSong() throws SongNullException {
        if (songReference == null || songReference.get() == null) {
            Song song = loadFromDisc();
            if (song == null) {
                throw new SongNullException("Song can't be loaded");
            }
            songReference = new WeakReference<>(song);
            return song;
        } else
            return songReference.get();
    }

    /**
     * Загружает инстанц прямо с диска
     * То есть, создаёт новый объект
     */
    private Song loadFromDisc() {
        return NBSDecoder.parse(file);
    }

    /**
     * Хеш для сонга
     * Нужен для табличек, так как на них не поместится всё название
     */
    public int getHash() {
        return Objects.hashCode(
                name,
                length,
                speed
        );
    }

    public ItemStack getSongStack() {
        return getSongStack(BukkitUtils.getRandomDisc());
    }

}
