package ru.spliterash.musicbox.customPlayers.abstracts;

import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;
import ru.spliterash.musicbox.customPlayers.models.MusicBoxSongPlayerModel;
import ru.spliterash.musicbox.customPlayers.models.RangePlayerModel;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class AbstractBlockPlayer extends PositionSongPlayer implements PositionPlayer {
    private final static Map<Location, AbstractBlockPlayer> players = new HashMap<>();
    private final MusicBoxSongPlayerModel musicBoxModel;
    private final RangePlayerModel rangePlayerModel;
    private final Location location;

    public AbstractBlockPlayer(IPlayList list, Location location, int range) {
        super(list.getCurrent().getSong());
        this.location = BukkitUtils.centerBlock(location);
        setRange(range);
        setTargetLocation(BukkitUtils.centerBlock(location));
        AbstractBlockPlayer oldBlock = players.put(getTargetLocation(), this);
        if (oldBlock != null)
            oldBlock.destroy();
        this.musicBoxModel = new MusicBoxSongPlayerModel(this, list, this::runNextSong);
        this.rangePlayerModel = new RangePlayerModel(musicBoxModel);
        musicBoxModel.runPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                while (!isDestroyed()) {
                    rangePlayerModel.tick();
                    every100MillisAsync();
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

    public static <T extends AbstractBlockPlayer> T findByLocation(Location location) {
        //noinspection unchecked
        return (T) players.get(BukkitUtils.centerBlock(location));
    }

    protected abstract Location getInfoSign();


    protected abstract void every100MillisAsync();

    protected abstract MusicBoxSongPlayer runNextSong(IPlayList list);

    public static <T extends AbstractBlockPlayer> Optional<T> findByInfoSign(Location location) {
        //noinspection unchecked
        return players
                .values()
                .stream()
                .filter(i -> i.getInfoSign() != null && i.getInfoSign().equals(location))
                .findFirst()
                .map(a -> (T) a);
    }

    @Override
    public Location getLocation() {
        return getTargetLocation();
    }

    @Override
    public int getRange() {
        return getDistance();
    }

    @Override
    public void setRange(int range) {
        setDistance(range);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void destroy() {
        if (!isDestroyed()) {
            super.destroy();
            players.values().remove(this);
            boolean normalEnd = musicBoxModel.isSongEndNormal();
            if (normalEnd)
                songEnd();
            Location infoSign = getInfoSign();
            if (infoSign != null) {
                BukkitUtils.runSyncTask(() -> {
                    if (!musicBoxModel.getPlayList().hasNext())
                        SignUtils.setPlayerOff(infoSign);
                    else if (!normalEnd)
                        SignUtils.setPlayerOff(infoSign);
                });
            }
            rangePlayerModel.destroy();
            musicBoxModel.destroy();

        }
    }

    /**
     * Вызывается в случае нормального завершения музыки
     */
    protected abstract void songEnd();
}
