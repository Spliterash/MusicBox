package ru.spliterash.musicbox.customPlayers.models;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import lombok.Getter;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.players.PlayerWrapper;

/**
 * Небольшая модель
 * Которая применима ко всем проигрывателям игроков
 */
@Getter
public class PlayerPlayerModel {

    private final PlayerWrapper wrapper;
    private final MusicBoxSongPlayerModel model;
    private String title;

    public PlayerPlayerModel(PlayerWrapper wrapper, MusicBoxSongPlayerModel model) {
        this.wrapper = wrapper;
        this.model = model;
        wrapper.setBarProgress(0);
        wrapper.setBarVisible(true);
        SongPlayer songPlay = model.getMusicBoxSongPlayer().getApiPlayer();
        title = songPlay.getSong().getTitle();
        wrapper.setBarTitle(Lang.CURRENT_PLAYNING.toString("{song}", title));
        songPlay.addPlayer(wrapper.getPlayer());
    }

    /**
     * Вызывается до воспроизведения следующего тика
     *
     * @param all     Кол-во тиков в мелодии
     * @param current Текущий тик
     */
    public void nextTick(int all, int current) {
        if (current == -1) {
            return;
        }
        double progress = (double) current / (double) all;
        if (current == 0) {
            title = model.getMusicBoxSongPlayer().getApiPlayer().getSong().getTitle();
            wrapper.setBarTitle(Lang.CURRENT_PLAYNING.toString("{song}", title));
        }
        if (progress >= 0 && progress <= 1) {
            wrapper.setBarProgress(progress);
        }
    }

    public void destroy() {
        wrapper.nullActivePlayer(getModel().getMusicBoxSongPlayer());
    }
}
