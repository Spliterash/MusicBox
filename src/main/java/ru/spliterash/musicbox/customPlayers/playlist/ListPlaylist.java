package ru.spliterash.musicbox.customPlayers.playlist;

import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.utils.classes.PeekList;
import ru.spliterash.musicbox.song.songContainers.types.SongContainer;

import java.util.List;

public class ListPlaylist implements IPlayList {
    private final PeekList<MusicBoxSong> peekList;

    public ListPlaylist(List<MusicBoxSong> songs) {
        this(songs, false);
    }

    /**
     * Создаёт PlayList из списка
     *
     * @param songs  Список музыки
     * @param hasEnd Если false то лист крутится без остановки
     */
    public ListPlaylist(List<MusicBoxSong> songs, boolean hasEnd) {
        if (songs.size() == 0)
            throw new RuntimeException("List can't be 0 size");
        this.peekList = new PeekList<>(songs, hasEnd);
    }

    public static ListPlaylist fromContainer(SongContainer container, boolean rand, boolean hasEnd) {
        return new ListPlaylist(rand ? container.getSongsShuffle() : container.getAllSongs(), hasEnd);
    }

    @Override
    public void next() {
        peekList.next();
    }

    @Override
    public List<MusicBoxSong> getNextSongs(int count) {
        return peekList.getNextElements(count);
    }


    @Override
    public boolean hasNext() {
        return peekList.hasNext();
    }

    @Override
    public boolean hasPrev() {
        return peekList.hasPrev();
    }

    @Override
    public List<MusicBoxSong> getPrevSongs(int count) {
        return peekList.getPrevElements(count);
    }

    @Override
    public MusicBoxSong getCurrent() {
        return peekList.current();
    }

    @Override
    public void back(int count) {
        for (int i = 0; i < count; i++) {
            peekList.prev();
        }
    }

    @Override
    public int getSongNum(MusicBoxSong song) {
        return peekList.getIndexOf(song);
    }

    @Override
    public void setSong(MusicBoxSong song) {
        peekList.moveTo(song);
    }
}
