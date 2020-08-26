package ru.spliterash.musicbox.customPlayers.playlist;

import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.utils.classes.PeekList;
import ru.spliterash.musicbox.utils.classes.SongContainer;

import java.util.Collections;
import java.util.List;

public class ListPlaylist implements IPlayList {
    private final PeekList<MusicBoxSong> peekList;

    public ListPlaylist(SongContainer model) {
        this(model.getSongs());
    }

    public ListPlaylist(List<MusicBoxSong> songs) {
        if (songs.size() == 0)
            throw new RuntimeException("List can't be 0 size");
        this.peekList = new PeekList<>(songs);
    }

    @Override
    public MusicBoxSong getNext() {
        return peekList.peek();
    }

    @Override
    public List<MusicBoxSong> getNextSongs(int count) {
        return peekList.getNextElements(count);
    }


    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public List<MusicBoxSong> getPreviousSong(int count) {
        List<MusicBoxSong> list = peekList.getPrevElements(count);
        Collections.reverse(list);
        return list;
    }

    @Override
    public MusicBoxSong getCurrent() {
        return peekList.current();
    }

    @Override
    public void back(int count) {
        for (int i = 0; i < count; i++) {
            peekList.peekPrev();
        }
    }
}
