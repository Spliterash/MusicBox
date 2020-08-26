package ru.spliterash.musicbox.customPlayers.models;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import lombok.Getter;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.gui.song.RewindGUI;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.util.function.Consumer;

@Getter
public class AllPlayerModel {
    private final MusicBoxSongPlayer musicBoxSongPlayer;
    private final MusicBoxSong currentSong;
    private final IPlayList playList;
    private final Consumer<IPlayList> nextSongRunnable;
    private boolean continuePlaylist = true;
    private boolean run = false;

    public AllPlayerModel(MusicBoxSongPlayer songPlayer, MusicBoxSong currentSong, IPlayList playList, Consumer<IPlayList> nextSongRunnable) {
        this.musicBoxSongPlayer = songPlayer;
        this.currentSong = currentSong;
        this.playList = playList;
        this.nextSongRunnable = nextSongRunnable;
    }

    public void runPlayer() {
        if (!run) {
            SongPlayer songPlayer = this.musicBoxSongPlayer.getApiPlayer();
            songPlayer.setAutoDestroy(true);
            songPlayer.setPlaying(true);
            run = true;
        }
    }

    /**
     * Вызывается при вызове {@link SongPlayer#destroy()}
     */
    public void destroy() {
        if (rewindGUI != null)
            rewindGUI.close();
        if (nextSongRunnable != null && continuePlaylist)
            nextSongRunnable.accept(playList);
    }

    /**
     * Полностью останавливает SongPlayer не воспроизводя следующую музыку из плейлиста
     */
    public void totalDestroy() {
        continuePlaylist = false;
        musicBoxSongPlayer.destroy();
    }

    private RewindGUI rewindGUI;

    /**
     * Создаёт GUI для перемотки этого плеера
     */
    public RewindGUI getRewind() {
        if (rewindGUI == null)
            rewindGUI = new RewindGUI(musicBoxSongPlayer);
        return rewindGUI;
    }
}
