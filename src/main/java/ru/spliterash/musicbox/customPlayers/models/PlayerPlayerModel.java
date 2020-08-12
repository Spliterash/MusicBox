package ru.spliterash.musicbox.customPlayers.models;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import lombok.Getter;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;

/**
 * Небольшая модель
 * Которая применима ко всем проигрывателям игроков
 */
@Getter
public class PlayerPlayerModel {
    private final PlayerSongPlayer songPlayer;
    private final MusicBoxSong song;
    private final PlayerWrapper player;

    public PlayerPlayerModel(PlayerSongPlayer songPlayer, MusicBoxSong song, PlayerWrapper player) {
        this.songPlayer = songPlayer;
        this.song = song;
        this.player = player;
        player.setBarProgress(0);
        player.setBarVisible(true);
        player.setBarTitle(Lang.CURRENT_PLAYNING.toString("{song}", getSong().getName()));
        SongPlayer songPlay = songPlayer.getSongPlayer();
        songPlay.setAutoDestroy(true);
        songPlay.addPlayer(player.getPlayer());
        songPlay.setPlaying(true);
    }

    /**
     * Вызывается до воспроизведения следующего тика
     *
     * @param all     Кол-во тиков в мелодии
     * @param current Текущий тик
     */
    public void nextTick(int all, int current) {
        if (current == -1) {
            player.setBarVisible(false);
            return;
        }
        double progress = (double) current / (double) all;
        if (progress >= 0 && progress <= 1) {
            player.setBarProgress(progress);
        }
    }

    public void destroy() {
        player.setBarVisible(false);
    }
}
