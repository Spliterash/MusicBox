package ru.spliterash.musicbox.song;

import com.cryptomorin.xseries.XMaterial;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.utils.*;
import ru.spliterash.musicbox.utils.nbt.NBTFactory;
import ru.spliterash.musicbox.utils.nbt.NbtConstants;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MusicBoxSong {

    private final File file;
    private final String name;
    private final Map<String, String> hoverMap = new HashMap<>();
    private final MusicBoxSongContainer container;
    private final short length;
    private final float speed;
    private transient WeakReference<Song> songReference;
    private final int hash;

    MusicBoxSong(File songFile, MusicBoxSongContainer container) {
        this.file = songFile;
        this.container = container;
        Song song = getSong();
        this.name = StringUtils.t(StringUtils.getOrEmpty(song.getTitle(), () -> FileUtils.getFilename(file.getName())));
        this.length = song.getLength();
        this.speed = song.getSpeed();
        this.hash = file.getPath().hashCode();
        String time = StringUtils.toHumanTime(getDuration());
        hoverMap.put("{length}", time);
        hoverMap.put("{author}", song.getAuthor());
        hoverMap.put("{original_author}", song.getOriginalAuthor());
        hoverMap.put("{name}", getName());
    }

    public int getDuration() {
        return (int) Math.floor(length / speed);
    }

    public ItemStack getSongStack(XMaterial material) {
        return getSongStack(material, Collections.emptyList(), false);
    }

    public ItemStack getSongStack(XMaterial material, List<String> extraLines, boolean glow) {
        return getSongStack(material, Lang.SONG_NAME.toString("{song}", getName()), extraLines, glow);
    }

    /**
     * @param material Какой материал использовать
     * @return Айтем с этим материалом
     */
    public ItemStack getSongStack(XMaterial material, String itemName, List<String> extraLines, boolean glow) {
        ItemStack stack = material.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(itemName);
        List<String> list = ArrayUtils.replaceOrRemove(Lang.SONG_LORE.toList(), hoverMap);
        list.addAll(extraLines);
        meta.setLore(list);
        stack.setItemMeta(meta);
        stack = NBTFactory.NBT_HANDLER.setNbt(stack, NbtConstants.NBT_NAME, getHash());
        if (glow)
            stack = ItemUtils.glow(stack);
        return stack;
    }

    /**
     * Смотрит есть ли Song в референсе
     * И если нету, то отдаёт загруженный с диска файл
     * <p>
     * Немного подвиснет сервак кнч, но зато оперативка чистая
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

    public ItemStack getSongStack() {
        return getSongStack(BukkitUtils.getRandomDisc());
    }

}
