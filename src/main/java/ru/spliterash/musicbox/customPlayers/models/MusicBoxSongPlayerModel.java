package ru.spliterash.musicbox.customPlayers.models;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import lombok.Getter;
import ru.spliterash.musicbox.customPlayers.abstracts.AbstractBlockPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.gui.song.SPControlGUI;
import ru.spliterash.musicbox.song.MusicBoxSong;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

@Getter
public class MusicBoxSongPlayerModel {
    private final static Set<MusicBoxSongPlayerModel> all = Collections.newSetFromMap(new WeakHashMap<>());
    private final MusicBoxSongPlayer musicBoxSongPlayer;
    private final IPlayList playList;
    private final Function<IPlayList, ? extends MusicBoxSongPlayer> nextSongRunnable;
    private boolean run = false;

    /**
     * @param songPlayer       плеер который связан с этой моделью
     * @param playList         плейлист который сейчас играет
     * @param nextSongRunnable как ставить следующую музыку из плейлиста
     */
    public MusicBoxSongPlayerModel(MusicBoxSongPlayer songPlayer, IPlayList playList, Function<IPlayList, ? extends MusicBoxSongPlayer> nextSongRunnable) {
        all.add(this);
        this.musicBoxSongPlayer = songPlayer;
        this.playList = playList;
        this.nextSongRunnable = nextSongRunnable;
    }

    public static void destroyAll() {
        all.forEach(a -> a.getMusicBoxSongPlayer().destroy());
        all.clear();
    }

    public MusicBoxSong getCurrentSong() {
        return playList.getCurrent();
    }

    public void runPlayer() {
        if (!run) {
            SongPlayer songPlayer = this.musicBoxSongPlayer.getApiPlayer();
            songPlayer.setPlaying(true);
            run = true;
        }
    }

    /**
     * Вызывается при вызове {@link SongPlayer#destroy()}
     */
    public void destroy() {
        if (controlGUI != null)
            controlGUI.close();
    }

    private SPControlGUI controlGUI;

    /**
     * Создаёт GUI для настройки этого SongPlayer'а
     */
    public SPControlGUI getControlGUI() {
        if (controlGUI == null)
            controlGUI = new SPControlGUI(this);
        return controlGUI;
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

    /**
     * Применяет следующий плейлист
     */
    private void acceptNext() {
        MusicBoxSongPlayer nextPlayer = nextSongRunnable.apply(playList);
        if (getMusicBoxSongPlayer() instanceof AbstractBlockPlayer) {
            RepeatMode repeatMode = ((AbstractBlockPlayer) getMusicBoxSongPlayer()).getRepeatModeValue();
            ((AbstractBlockPlayer) nextPlayer).setRepeatModeValue(repeatMode);
        } else {
            nextPlayer.getApiPlayer().setRepeatMode(getMusicBoxSongPlayer().getApiPlayer().getRepeatMode());
        }
        nextPlayer.getApiPlayer().setVolume(getMusicBoxSongPlayer().getApiPlayer().getVolume());
        if (nextPlayer != null && controlGUI != null)
            controlGUI.openNext(nextPlayer.getMusicBoxModel());
    }

    /**
     * Вызывается из event'a
     */
    public void onSongEnd() {
        if (!(this.getMusicBoxSongPlayer() instanceof PlayerSongPlayer)) startNext();
        else getMusicBoxSongPlayer().destroy();
    }

    private boolean nextCreated = false;

    public void playSong(int songNumber) {
        getMusicBoxSongPlayer().getApiPlayer().playSong(songNumber);
        if (controlGUI != null)
            controlGUI.openNext(getMusicBoxSongPlayer().getMusicBoxModel());
    }

    /**
     * Завершилась ли музыка сама по себе, или её destroy()
     */
    private boolean songEndNormal = false;

    /**
     * Возможно костыль, но сначало нужно destroy SongPlayer а потом его модель
     */
    public void pingSongEnded() {
        songEndNormal = true;
    }

    public void startNext() {
        if (playList.tryNext()) {
            if (getMusicBoxSongPlayer() instanceof AbstractBlockPlayer) {
                RepeatMode repeatMode = ((AbstractBlockPlayer) getMusicBoxSongPlayer()).getRepeatModeValue();
                switch(repeatMode) {
                    case ALL:
                        createNextPlayer();
                        break;
                    case ONE:
                        getMusicBoxSongPlayer().getApiPlayer().setTick((short) 0);
                        break;
                    case NO:
                        playList.back(1);
                        getMusicBoxSongPlayer().destroy();
                        break;
                }
            } else {
                playSong(playList.getSongNum(playList.getCurrent()));
            }
        } else
            getMusicBoxSongPlayer().destroy();
    }

    public void createNextPlayer() {
        nextCreated = true;
        acceptNext();
        getMusicBoxSongPlayer().destroy();
    }
}
