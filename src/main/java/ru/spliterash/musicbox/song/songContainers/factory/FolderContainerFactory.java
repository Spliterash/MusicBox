package ru.spliterash.musicbox.song.songContainers.factory;

import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.song.songContainers.SongContainerFactory;
import ru.spliterash.musicbox.song.songContainers.containers.FolderSongContainer;

public class FolderContainerFactory implements SongContainerFactory<FolderSongContainer> {
    public static final String NAME = "CHEST";
    @Override
    public String getKey() {
        return NAME;
    }

    @Override
    public FolderSongContainer parseContainer(int id) {
        return MusicBoxSongManager
                .findContainerById(id)
                .map(FolderSongContainer::new)
                .orElse(null);
    }
}
