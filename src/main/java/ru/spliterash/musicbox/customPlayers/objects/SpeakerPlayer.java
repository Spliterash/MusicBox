package ru.spliterash.musicbox.customPlayers.objects;

import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.models.PlayerPlayerModel;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

/**
 * Проигрыватель для игрока
 * Но его слышат все вокруг
 * TODO После того как разработчик добавит нужную мне фичу
 */
@Getter
public class SpeakerPlayer extends EntitySongPlayer implements PlayerSongPlayer {
    private final PlayerPlayerModel model;

    public SpeakerPlayer(MusicBoxSong musicBoxSong, PlayerWrapper instance) {
        super(musicBoxSong.getSong());
        setEntity(instance.getPlayer());
        this.model = new PlayerPlayerModel(this, musicBoxSong, instance);
    }

    @Override
    public MusicBoxSong getMusicBoxSong() {
        return model.getSong();
    }

    @Override
    public void destroy() {
        super.destroy();
        model.destroy();
    }

    @Override
    public void playTick(Player player, int tick) {
        super.playTick(player, tick);
        if (player.equals(model.getPlayer().getPlayer())) {
            model.nextTick(getSong().getLength(), tick);
            spawnNote(tick, player);
        }
    }

    private void spawnNote(int tick, Player player) {
        //Если это не пустой тик то
        if (song
                .getLayerHashMap()
                .values()
                .stream()
                .anyMatch(l -> l.getNote(tick) != null)
        ) {
            Location spawnLocation = player.getLocation().add(0, 2.3, 0);
            player.getWorld().spawnParticle(Particle.NOTE, spawnLocation, 1, 0, 0, 0, 0, null);
        }
    }
}
