package ru.spliterash.musicbox.customPlayers.models;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import lombok.Getter;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.gui.song.RewindGUI;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

@Getter
public class MusicBoxSongPlayerModel {
    private final MusicBoxSongPlayer musicBoxSongPlayer;
    private final IPlayList playList;
    private final Consumer<IPlayList> nextSongRunnable;
    private boolean continuePlaylist = true;
    private boolean run = false;

    /**
     * @param songPlayer       плеер который связан с этой моделью
     * @param playList         плейлист который сейчас играет
     * @param nextSongRunnable как ставить следующую музыку из плейлиста
     */
    public MusicBoxSongPlayerModel(MusicBoxSongPlayer songPlayer, IPlayList playList, Consumer<IPlayList> nextSongRunnable) {
        this.musicBoxSongPlayer = songPlayer;
        this.playList = playList;
        this.nextSongRunnable = nextSongRunnable;
    }

    public MusicBoxSong getCurrentSong() {
        return playList.getCurrent();
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
        if (nextSongRunnable != null && continuePlaylist && playList.hasNext()) {
            playList.next();
            nextSongRunnable.accept(playList);
        }
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
    // Немного чернухи

    private static final Field lockField;

    private static final Field playersField;

    static {
        try {
            playersField = SongPlayer.class.getDeclaredField("playerList");
            lockField = SongPlayer.class.getDeclaredField("lock");
            playersField.setAccessible(true);
            lockField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private Lock getLock() throws IllegalAccessException {
        return (Lock) lockField.get(musicBoxSongPlayer);
    }

    private Map<UUID, Boolean> getPlayers() throws IllegalAccessException {
        //noinspection unchecked
        return (Map<UUID, Boolean>) playersField.get(musicBoxSongPlayer);
    }

    public void setPlayers(Collection<UUID> players) {
        try {
            Map<UUID, Boolean> map = getPlayers();
            if (map.keySet().containsAll(players))
                return;
            Lock lock = getLock();
            lock.lock();
            try {

                map.clear();
                players.forEach(p -> map.put(p, true));
            } finally {
                lock.unlock();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
