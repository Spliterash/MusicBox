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

import java.util.HashMap;
import java.util.Map;

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
    public SignPlayer(IPlayList list, Sign sign) {
        super(list.getNext().getSong());
        Location location = sign.getLocation();
        this.musicBoxModel = new MusicBoxSongPlayerModel(this, list, l -> new SignPlayer(l, sign));
        this.rangePlayerModel = new RangePlayerModel(musicBoxModel);
        this.location = BukkitUtils.centerBlock(location);
        SignPlayer oldBlock = players.put(this.location, this);
        if (oldBlock != null)
            oldBlock.totalDestroy();
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
    public int getRange() {
        return getDistance();
    }

    @Override
    public void setRange(int range) {
        setDistance(range);
    }
}
