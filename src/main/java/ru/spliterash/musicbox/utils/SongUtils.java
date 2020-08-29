package ru.spliterash.musicbox.utils;

import com.xxmicloxx.NoteBlockAPI.model.Note;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import lombok.experimental.UtilityClass;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class SongUtils {
    public boolean containsNewInstrument(Song song) {
        return MusicBox.getInstance().getConfigObject().isPrintNewInstrument() && song
                .getLayerHashMap()
                .values()
                .stream()
                .map(l -> l.getNotesAtTicks().values())
                .flatMap(Collection::stream)
                .map(Note::getInstrument)
                .anyMatch(n -> n >= 10);
    }

    /**
     * Генерирует список проигрывания из текущего Playlist'а
     */
    public List<String> generatePlaylistLore(IPlayList list, int prevCount, int postCount) {
        List<String> lore = new ArrayList<>(prevCount + postCount + 1);
        for (MusicBoxSong song : list.getPrevSong(prevCount)) {
            lore.add(Lang.ANOTHER_PLAYLIST_SONG.toString(
                    "{song}", song.getName(),
                    "{num}", String.valueOf(list.getSongNum(song) + 1)
            ));
        }
        lore.add(Lang.CURRENT_PLAYLIST_SONG.toString(
                "{song}", list.getCurrent().getName(),
                "{num}", String.valueOf(list.getSongNum(list.getCurrent()) + 1)
        ));
        for (MusicBoxSong song : list.getNextSongs(postCount)) {
            lore.add(Lang.ANOTHER_PLAYLIST_SONG.toString(
                    "{song}", song.getName(),
                    "{num}", String.valueOf(list.getSongNum(song) + 1)
            ));
        }
        return lore;
    }


    public Consumer<IPlayList> nextPlayerSong(PlayerWrapper wrapper) {
        return wrapper::play;
    }
}
