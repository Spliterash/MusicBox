package ru.spliterash.musicbox.customPlayers.models;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class RangePlayerModel {
    private final MusicBoxSongPlayerModel musicBoxModel;
    private final Set<UUID> players = new HashSet<>();
    private final int destroyMillis;
    private int emptyMillis = 0;

    public RangePlayerModel(MusicBoxSongPlayerModel musicBoxModel) {
        this.musicBoxModel = musicBoxModel;
        destroyMillis = MusicBox.getInstance().getConfigObject().getAutoDestroy() * 1000;
        players.addAll(getSongPlayer().getPlayers());
    }

    public PositionPlayer getSongPlayer() {
        return (PositionPlayer) musicBoxModel.getMusicBoxSongPlayer();
    }

    public void destroy() {
        // Потом может быть
    }

    /**
     * Обновление игроков
     * Вызывается в асинхронне каждые 100 милисов
     */
    public void tick() {
        Set<UUID> canHear = Bukkit
                .getOnlinePlayers()
                .stream()
                .filter(p -> p.getWorld().equals(getSongPlayer().getLocation().getWorld()))
                .filter(p -> p.getLocation().distance(getSongPlayer().getLocation()) < getSongPlayer().getRange() + 10)
                .map(Entity::getUniqueId)
                .collect(Collectors.toSet());
        if (destroyMillis > 0 && canHear.size() == 0) {
            if (emptyMillis >= destroyMillis) {
                getSongPlayer().destroy();
                return;
            }
            emptyMillis += 100;
        } else
            emptyMillis = 0;
        getMusicBoxModel().setPlayers(canHear);
    }
}
