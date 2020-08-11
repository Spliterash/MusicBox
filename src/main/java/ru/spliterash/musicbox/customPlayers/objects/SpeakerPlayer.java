package ru.spliterash.musicbox.customPlayers.objects;

import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import lombok.Getter;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.models.PlayerPlayerModel;
import ru.spliterash.musicbox.players.PlayerInstance;
import ru.spliterash.musicbox.song.MusicBoxSong;

@Getter
public class SpeakerPlayer extends EntitySongPlayer implements PlayerSongPlayer {
    private final PlayerPlayerModel model;

    public SpeakerPlayer(MusicBoxSong musicBoxSong, PlayerInstance instance) {
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
}
