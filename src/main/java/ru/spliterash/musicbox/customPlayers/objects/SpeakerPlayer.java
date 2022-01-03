package ru.spliterash.musicbox.customPlayers.objects;

import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;
import ru.spliterash.musicbox.customPlayers.models.MusicBoxSongPlayerModel;
import ru.spliterash.musicbox.customPlayers.models.PlayerPlayerModel;
import ru.spliterash.musicbox.customPlayers.models.RangePlayerModel;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.SongUtils;

/**
 * Проигрыватель для игрока
 * Но его слышат все вокруг
 */
@Getter
public class SpeakerPlayer extends EntitySongPlayer implements PlayerSongPlayer, PositionPlayer {

    private final MusicBoxSongPlayerModel musicBoxModel;
    private final PlayerPlayerModel model;
    private final RangePlayerModel rangeModel;
    private final BukkitTask task;
    private final PlayerWrapper owner;

    public SpeakerPlayer(IPlayList list, PlayerWrapper wrapper) {
        super(list.getCurrent().getSong());
        this.musicBoxModel = new MusicBoxSongPlayerModel(this, list, SongUtils.nextPlayerSong(wrapper));
        this.model = new PlayerPlayerModel(wrapper, musicBoxModel);
        this.rangeModel = new RangePlayerModel(musicBoxModel);
        this.owner = wrapper;
        setEntity(wrapper.getPlayer());
        setRange(MusicBox.getInstance().getConfigObject().getSpeakerRadius());

        musicBoxModel.runPlayer();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                while (!isDestroyed()) {
                    rangeModel.tick();
                    try {
                        //noinspection BusyWait
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }.runTaskAsynchronously(MusicBox.getInstance());
    }

    @Override
    public void destroy() {
        if (!isDestroyed()) {
            try {
                super.destroy();
            } catch (IllegalPluginAccessException ex) {
                // ПОФИК
            }
            rangeModel.destroy();
            model.destroy();
            musicBoxModel.destroy();
        }
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
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
    public int getRange() {
        return super.getDistance();
    }

    @Override
    public void setRange(int range) {
        super.setDistance(range);
    }


}
