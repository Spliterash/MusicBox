package ru.spliterash.musicbox.song.songContainers.factory;

import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.song.songContainers.SongContainerFactory;
import ru.spliterash.musicbox.song.songContainers.containers.SingletonContainer;

public class SingletonContainerFactory implements SongContainerFactory<SingletonContainer> {
    public static String NAME = "ID";

    @Override
    public String getKey() {
        return NAME;
    }

    @Override
    public SingletonContainer parseContainer(int id) {
        return MusicBoxSongManager
                .findSongByHash(id)
                .map(SingletonContainer::new)
                .orElse(null);
    }
}
