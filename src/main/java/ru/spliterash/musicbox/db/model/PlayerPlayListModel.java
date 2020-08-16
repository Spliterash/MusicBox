package ru.spliterash.musicbox.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PlayerPlayListModel {
    private int id;
    private final UUID owner;
    private String name;
    private final List<MusicBoxSong> songs;


    public Optional<PlayerWrapper> getOwnerWrapper() {
        Player player = Bukkit.getPlayer(owner);
        if (player == null)
            return Optional.empty();
        else
            return Optional.of(PlayerWrapper.getInstance(player));
    }
}
