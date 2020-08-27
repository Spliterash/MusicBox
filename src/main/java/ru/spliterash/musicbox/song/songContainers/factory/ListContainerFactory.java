package ru.spliterash.musicbox.song.songContainers.factory;

import ru.spliterash.musicbox.db.DatabaseLoader;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.song.songContainers.SongContainerFactory;

public class ListContainerFactory implements SongContainerFactory<PlayerPlayListModel> {
    @Override
    public String getKey() {
        return "LIST";
    }

    @Override
    public PlayerPlayListModel parseContainer(int id) {
        return DatabaseLoader.getBase().getPlayListById(id);
    }
}
