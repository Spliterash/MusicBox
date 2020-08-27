package ru.spliterash.musicbox.song.songContainers.factory;

import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.song.songContainers.SongContainer;
import ru.spliterash.musicbox.song.songContainers.SongContainerFactory;
import ru.spliterash.musicbox.song.songContainers.containers.SingletonContainer;

public class IdContainerFactory implements SongContainerFactory<SongContainer> {
    @Override
    public String getKey() {
        return "ID";
    }

    @Override
    public SongContainer parseContainer(int id) {
        return MusicBoxSongManager
                .findSongByHash(id)
                .map(SingletonContainer::new)
                .orElse(null);
    }
}
