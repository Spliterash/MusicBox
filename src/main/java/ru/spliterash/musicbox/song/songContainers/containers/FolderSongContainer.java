package ru.spliterash.musicbox.song.songContainers.containers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.spliterash.musicbox.song.songContainers.SongContainer;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongContainer;
import ru.spliterash.musicbox.song.songContainers.factory.FolderContainerFactory;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class FolderSongContainer implements SongContainer {
    private final MusicBoxSongContainer container;

    @Override
    public String getNameId() {
        return FolderContainerFactory.NAME + ":" + container.getHash();
    }

    @Override
    public List<MusicBoxSong> getSongs() {
        return container.getAllSongs();
    }

}
