package ru.spliterash.musicbox.song.songContainers.factory;

import ru.spliterash.musicbox.song.MusicBoxSongContainer;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.song.songContainers.SongContainerFactory;

public class FolderContainerFactory implements SongContainerFactory<MusicBoxSongContainer> {
    public static final String NAME = "CHEST";

    @Override
    public String getKey() {
        return NAME;
    }

    @Override
    public MusicBoxSongContainer parseContainer(int id) {
        return MusicBoxSongManager
                .findContainerById(id)
                .orElse(null);
    }
}
