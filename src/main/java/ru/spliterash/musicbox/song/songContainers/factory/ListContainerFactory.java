package ru.spliterash.musicbox.song.songContainers.factory;

import ru.spliterash.musicbox.db.DatabaseLoader;
import ru.spliterash.musicbox.song.songContainers.SongContainer;
import ru.spliterash.musicbox.song.songContainers.SongContainerFactory;

public class ListContainerFactory implements SongContainerFactory<SongContainer> {
    @Override
    public String getKey() {
        return "LIST";
    }

    @Override
    public SongContainer parseContainer(int id) {
        return DatabaseLoader.getBase().getPlayListById(id);
    }
}
