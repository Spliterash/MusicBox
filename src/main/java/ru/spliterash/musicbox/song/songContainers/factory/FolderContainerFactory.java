package ru.spliterash.musicbox.song.songContainers.factory;

import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.song.songContainers.SongContainerFactory;
import ru.spliterash.musicbox.song.songContainers.containers.FolderSongContainer;

public class FolderContainerFactory implements SongContainerFactory<FolderSongContainer> {
    @Override
    public String getKey() {
        return "FOLDER";
    }

    @Override
    public FolderSongContainer parseContainer(int id) {
        return MusicBoxSongManager
                .findContainerById(id)
                .map(FolderSongContainer::new)
                .orElse(null);
    }
}
