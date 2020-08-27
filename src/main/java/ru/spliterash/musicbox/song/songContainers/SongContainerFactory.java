package ru.spliterash.musicbox.song.songContainers;

import java.util.Optional;

public interface SongContainerFactory<T extends SongContainer> {
    String getKey();

    T parseContainer(int id);
}
