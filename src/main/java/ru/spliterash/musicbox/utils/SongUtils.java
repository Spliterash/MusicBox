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
import java.util.Collections;
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

    private String getNum(int num) {
        if (num > -1)
            return Lang.PLAYLIST_SONG_NUM.toString("{num}", String.valueOf(num + 1));
        else
            return "";
    }

    /**
     * Генерирует список проигрывания из текущего Playlist'а
     */
    public List<String> generatePlaylistLore(IPlayList list, int prevCount, int postCount) {
        List<String> lore = new ArrayList<>(prevCount + postCount + 1);
        List<MusicBoxSong> prevList = list.getPrevSongs(prevCount);
        Collections.reverse(prevList);
        for (MusicBoxSong song : prevList) {
            String num = getNum(list.getSongNum(song));
            lore.add(Lang.ANOTHER_PLAYLIST_SONG.toString(
                    "{song}", song.getName(),
                    "{num}", num));
        }
        {
            String num = getNum(list.getSongNum(list.getCurrent()));
            lore.add(Lang.CURRENT_PLAYLIST_SONG.toString(
                    "{song}", list.getCurrent().getName(),
                    "{num}", num));
        }
        for (MusicBoxSong song : list.getNextSongs(postCount)) {
            String num = getNum(list.getSongNum(song));
            lore.add(Lang.ANOTHER_PLAYLIST_SONG.toString(
                    "{song}", song.getName(),
                    "{num}", num
            ));
        }
        return lore;
    }


    public Consumer<IPlayList> nextPlayerSong(PlayerWrapper wrapper) {
        return wrapper::play;
    }
}
