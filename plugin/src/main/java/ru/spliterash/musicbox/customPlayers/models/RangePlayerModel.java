package ru.spliterash.musicbox.customPlayers.models;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;
import ru.spliterash.musicbox.customPlayers.objects.SpeakerPlayer;
import ru.spliterash.musicbox.players.PlayerWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class RangePlayerModel {
    private final MusicBoxSongPlayerModel musicBoxModel;
    private final Set<UUID> players = new HashSet<>();
    private int destroyMillis;
    private int emptyMillis = 0;

    public RangePlayerModel(MusicBoxSongPlayerModel musicBoxModel) {
        this.musicBoxModel = musicBoxModel;
        destroyMillis = MusicBox.getInstance().getConfigObject().getAutoDestroy() * 1000;
        players.addAll(getSongPlayer().getPlayers());
    }

    /**
     * Устанавливает новое значение после которого SP будет уничтожен
     */
    public void setAutoDestroyMillis(int millis) {
        this.destroyMillis = millis;
        emptyMillis = 0;
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
        Stream<UUID> ownerStream;
        MusicBoxSongPlayer sp = musicBoxModel.getMusicBoxSongPlayer();
        if (sp instanceof SpeakerPlayer)
            ownerStream = Stream.of(((SpeakerPlayer) sp).getOwner().getPlayer()).map(Entity::getUniqueId);
        else
            ownerStream = Stream.empty();

        Set<UUID> canHear = Stream.concat(Bukkit
                        .getOnlinePlayers()
                        .stream()
                        .filter(p -> PlayerWrapper.getInstance(p).canHearMusic())
                        .filter(p -> p.getWorld().equals(getSongPlayer().getLocation().getWorld()))
                        .filter(p -> p.getLocation().distanceSquared(getSongPlayer().getLocation()) < Math.pow(getSongPlayer().getRange() + 10, 2))
                        .map(Entity::getUniqueId), ownerStream)
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
