package ru.spliterash.musicbox.customPlayers.objects;

import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;
import ru.spliterash.musicbox.customPlayers.models.AllPlayerModel;
import ru.spliterash.musicbox.customPlayers.models.PlayerPlayerModel;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.utils.SongUtils;

/**
 * Проигрыватель для игрока
 * Но его слышат все вокруг
 */
@Getter
public class SpeakerPlayer extends EntitySongPlayer implements PlayerSongPlayer, PositionPlayer {

    private final AllPlayerModel musicBoxModel;
    private final PlayerPlayerModel model;

    public SpeakerPlayer(MusicBoxSong song, IPlayList list, PlayerWrapper wrapper) {
        super(song.getSong());
        this.musicBoxModel = new AllPlayerModel(this, song, list, SongUtils.getRunNextRunnable(wrapper, list));
        this.model = new PlayerPlayerModel(wrapper, musicBoxModel);
        setEntity(wrapper.getPlayer());
        musicBoxModel.runPlayer();

    }

    @Override
    public void destroy() {
        super.destroy();
        model.destroy();
        musicBoxModel.destroy();
    }

    @Override
    public void playTick(Player player, int tick) {
        super.playTick(player, tick);
        if (player.equals(model.getWrapper().getPlayer())) {
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
            player.getWorld().spawnParticle(Particle.NOTE, spawnLocation, 1);
        }
    }

    @Override
    public Location getLocation() {
        return model.getWrapper().getPlayer().getLocation();
    }

    @Override
    public void setLocation(Location location) {
        throw new UnsupportedOperationException("Can't set location for player player");
    }

    @Override
    public int getRange() {
        return super.getDistance();
    }

    @Override
    public void setRange(int range) {
        super.setDistance(range);
    }
}
