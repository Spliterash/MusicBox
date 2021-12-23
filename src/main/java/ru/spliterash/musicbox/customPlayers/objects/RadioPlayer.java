package ru.spliterash.musicbox.customPlayers.objects;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.models.MusicBoxSongPlayerModel;
import ru.spliterash.musicbox.customPlayers.models.PlayerPlayerModel;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.SongUtils;

/**
 * Проигрыватель для игрока
 * Если игрок захочет послушать музыку никого не напрягая
 */
@Getter
public class RadioPlayer extends RadioSongPlayer implements PlayerSongPlayer {
    private final PlayerPlayerModel model;
    private final MusicBoxSongPlayerModel musicBoxModel;


    public RadioPlayer(IPlayList list, PlayerWrapper wrapper) {
        super(list.getCurrent().getSong());
        if (MusicBox.getInstance().getConfigObject().isExtendedOctavesRange()) this.setEnable10Octave(true);
        this.musicBoxModel = new MusicBoxSongPlayerModel(this, list, SongUtils.nextPlayerSong(wrapper));
        this.model = new PlayerPlayerModel(wrapper, musicBoxModel);
        musicBoxModel.runPlayer();

    }


    @Override
    public void destroy() {
        if (!isDestroyed()) {
            super.destroy();
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
        }
    }
}
