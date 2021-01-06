package ru.spliterash.musicbox.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.db.DatabaseLoader;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.songContainers.types.SongContainer;
import ru.spliterash.musicbox.song.songContainers.factory.ListContainerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PlayerPlayListModel implements SongContainer {
    private int id;
    private final UUID owner;
    private String name;
    private final List<MusicBoxSong> songs = new LinkedList<>();

    /**
     * Возращает владельца если тот в сети
     */
    public Optional<PlayerWrapper> getOwnerWrapper() {
        Player player = Bukkit.getPlayer(owner);
        if (player == null)
            return Optional.empty();
        else
            return Optional.of(PlayerWrapper.getInstance(player));
    }

    public void save() {
        DatabaseLoader.getBase().savePlayList(this);
    }

    public void delete() {
        DatabaseLoader.getBase().deleteMe(this);
    }

    @Override
    public String getNameId() {
        return ListContainerFactory.NAME + ":" + id;
    }

}
