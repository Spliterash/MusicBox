package ru.spliterash.musicbox.utils.song;

import com.cryptomorin.xseries.XMaterial;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.spliterash.musicbox.utils.exceptions.SongInitializationException;
import ru.spliterash.musicbox.utils.utils.FileUtils;
import ru.spliterash.musicbox.utils.utils.StringUtils;

import java.io.File;
import java.lang.ref.WeakReference;

@Getter
public class MusicBoxSong {
    private final File file;
    private final String name;
    private final String author;
    private WeakReference<Song> songReference;

    MusicBoxSong(File songFile) throws SongInitializationException {
        this.file = songFile;
        Song song = getSongException();
        this.name = StringUtils.getOrEmpty(song.getTitle(), () -> FileUtils.getFilename(file.getName()));
        this.author = song.getAuthor();
    }

    public Song getSong() {
        try {
            return getSongException();
        } catch (SongInitializationException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack getSongStack(XMaterial material) {
        ItemStack stack = material.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Lang);
    }

    /**
     * Смотрит есть ли Song в референсе
     * И если нету, то отдаёт загруженный с диска файл
     */
    public Song getSongException() throws SongInitializationException {
        if (songReference == null || songReference.get() == null) {
            Song song = loadFromDisc();
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
}
