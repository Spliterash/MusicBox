package ru.spliterash.musicbox.utils;

import com.xxmicloxx.NoteBlockAPI.model.Note;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import lombok.experimental.UtilityClass;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.players.PlayerWrapper;

import java.util.Collection;
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


    public Consumer<IPlayList> nextPlayerSong(PlayerWrapper wrapper) {
        return wrapper::play;
    }
}
