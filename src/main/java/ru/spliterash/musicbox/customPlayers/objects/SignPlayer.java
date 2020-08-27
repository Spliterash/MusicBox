package ru.spliterash.musicbox.customPlayers.objects;

import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;
import ru.spliterash.musicbox.customPlayers.models.MusicBoxSongPlayerModel;
import ru.spliterash.musicbox.customPlayers.models.RangePlayerModel;
import ru.spliterash.musicbox.utils.BukkitUtils;

import java.util.*;

@Getter
public class SignPlayer extends PositionSongPlayer implements PositionPlayer {
    private final static Map<Location, SignPlayer> players = new HashMap<>();
    private final MusicBoxSongPlayerModel musicBoxModel;
    private final Location location;
    private final RangePlayerModel rangePlayerModel;

    /**
     * Создаёт новый проигрыватель для таблички
     *
     * @param list Плейлист который к ней привязан
     * @param sign Табличка к которой привязана эта музыка
     */
    public SignPlayer(IPlayList list, int range, Sign sign) {
        super(list.getCurrent().getSong());
        Location location = sign.getLocation();
        setRange(range);
        this.musicBoxModel = new MusicBoxSongPlayerModel(this, list, l -> new SignPlayer(l, range, sign));
        this.rangePlayerModel = new RangePlayerModel(musicBoxModel);
        this.location = BukkitUtils.centerBlock(location);
        SignPlayer oldBlock = players.put(this.location, this);
        if (oldBlock != null)
            oldBlock.destroy();
        musicBoxModel.runPlayer();
    }

    @Override
    public void destroy() {
        super.destroy();
        players.values().remove(this);
        rangePlayerModel.destroy();
        musicBoxModel.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public int getRange() {
        return getDistance();
    }

    @Override
    public void setRange(int range) {
        setDistance(range);
    }
}
