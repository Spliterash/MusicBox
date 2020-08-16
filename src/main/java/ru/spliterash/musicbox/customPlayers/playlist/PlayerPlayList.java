package ru.spliterash.musicbox.customPlayers.playlist;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.utils.ArrayUtils;
import ru.spliterash.musicbox.utils.classes.PeekList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class PlayerPlayList implements IPlayList {
    private int id;
    private final UUID owner;
    private String name;
    private boolean rand = false;
    private final List<MusicBoxSong> songs;
    private final PeekList<MusicBoxSong> peekList;

    public PlayerPlayList(int id, UUID owner, String name, List<MusicBoxSong> songs) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.songs = songs;
        this.peekList = new PeekList<>(songs);
    }

    public Optional<PlayerWrapper> getOwnerWrapper() {
        Player player = Bukkit.getPlayer(owner);
        if (player == null)
            return Optional.empty();
        else
            return Optional.of(PlayerWrapper.getInstance(player));
    }

    @Override
    public MusicBoxSong getNext() {
        if (rand)
            return ArrayUtils.getRandom(songs);
        else
            return peekList.peek();
    }
}
