package ru.spliterash.musicbox.customPlayers.playlist;

import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.utils.classes.PeekList;

import java.util.List;

public class PlayerPlayListImpl implements IPlayList {
    private final PeekList<MusicBoxSong> peekList;

    public PlayerPlayListImpl(PlayerPlayListModel model) {
        this.peekList = new PeekList<>(model.getSongs());
    }

    @Override
    public MusicBoxSong getNext() {
        return peekList.peek();
    }

    @Override
    public List<MusicBoxSong> getNextFiveSong() {
        return peekList.getNextElements(5);
    }

    @Override
    public boolean hasNext() {
        return true;
    }
}
