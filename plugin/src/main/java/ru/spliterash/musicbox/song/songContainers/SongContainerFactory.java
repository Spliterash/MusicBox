package ru.spliterash.musicbox.song.songContainers;

import ru.spliterash.musicbox.song.songContainers.types.SongContainer;

public interface SongContainerFactory<T extends SongContainer> {
    String getKey();

    T parseContainer(int id);
}
