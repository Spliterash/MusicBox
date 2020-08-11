package ru.spliterash.musicbox.customPlayers.objects;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import lombok.Getter;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.models.PlayerPlayerModel;
import ru.spliterash.musicbox.players.PlayerInstance;
import ru.spliterash.musicbox.song.MusicBoxSong;

/**
 * Проигрыватель для игрока
 * Если игрок захочет послушать музыку никого не напрягая
 */
@Getter
public class RadioPlayer extends RadioSongPlayer implements PlayerSongPlayer {
    private final PlayerPlayerModel model;

    public RadioPlayer(MusicBoxSong musicBoxSong, PlayerInstance instance) {
        super(musicBoxSong.getSong());
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
}
