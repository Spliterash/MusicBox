package ru.spliterash.musicbox.song;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Objects;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.spliterash.musicbox.utils.ArrayUtils;
import ru.spliterash.musicbox.utils.FileUtils;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.utils.StringUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

@Getter
public class MusicBoxSong {

    private final File file;
    private final String name;
    private final HashMap<String, String> hoverMap;
    private WeakReference<Song> songReference;

    MusicBoxSong(File songFile) {
        this.file = songFile;
        Song song = getSong();
        this.name = StringUtils.getOrEmpty(song.getTitle(), () -> FileUtils.getFilename(file.getName()));
        this.hoverMap = new HashMap<>();
        hoverMap.put("{length}", String.valueOf((int) Math.floor(song.getLength() / 20D)));
        hoverMap.put("{author}", song.getAuthor());
        hoverMap.put("{original_author}", song.getOriginalAuthor());
    }

    /**
     * @param material Какой материал использовать
     * @return Айтем с этим материалом
     */
    public ItemStack getSongStack(XMaterial material) {
        ItemStack stack = material.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Lang.SONG_NAME.toString("{song}", getName()));
        meta.setLore(ArrayUtils.replaceOrRemove(Lang.SONG_LORE.toList(), hoverMap));
        stack.setItemMeta(meta);
        NBTEditor.set(stack, getHash(), SongManager.NBT_NAME);
        return stack;
    }

    /**
     * Смотрит есть ли Song в референсе
     * И если нету, то отдаёт загруженный с диска файл
     */
    public Song getSong() throws SongNullException{
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
                hoverMap.get("{length}"),
                hoverMap.get("{author}"),
                hoverMap.get("{original_author}")
        );
    }
}
