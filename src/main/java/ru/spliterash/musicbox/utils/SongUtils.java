package ru.spliterash.musicbox.utils;

import com.xxmicloxx.NoteBlockAPI.model.Note;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import lombok.experimental.UtilityClass;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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

    public String getNum(int num) {
        if (num > -1)
            return Lang.PLAYLIST_SONG_NUM.toString("{num}", String.valueOf(num + 1));
        else
            return "";
    }

    public String getSongName(int i, MusicBoxSong song, boolean glow) {
        String num = getNum(i);
        return (glow ? Lang.CURRENT_PLAYLIST_SONG : Lang.ANOTHER_PLAYLIST_SONG).toString(
                "{song}", song.getName(),
                "{num}", num);
    }

    /**
     * Генерирует список проигрывания из текущего Playlist'а
     */
    public List<String> generatePlaylistLore(IPlayList list, int prevCount, int postCount) {
        List<String> lore = new ArrayList<>(prevCount + postCount + 1);
        List<MusicBoxSong> prevList = list.getPrevSongs(prevCount);
        Collections.reverse(prevList);
        for (MusicBoxSong song : prevList) {
            lore.add(getSongName(list.getSongNum(song), song, false));
        }
        {
            MusicBoxSong current = list.getCurrent();
            lore.add(getSongName(list.getSongNum(current), current, true));
        }
        for (MusicBoxSong song : list.getNextSongs(postCount)) {
            lore.add(getSongName(list.getSongNum(song), song, false));
        }
        return lore;
    }


    public Function<IPlayList, PlayerSongPlayer> nextPlayerSong(PlayerWrapper wrapper) {
        return list -> {
            wrapper.play(list);
            return wrapper.getActivePlayer();
        };
    }
}
