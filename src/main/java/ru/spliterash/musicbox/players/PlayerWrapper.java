package ru.spliterash.musicbox.players;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.MusicBoxConfig;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.objects.RadioPlayer;
import ru.spliterash.musicbox.customPlayers.objects.SpeakerPlayer;
import ru.spliterash.musicbox.customPlayers.playlist.ListPlaylist;
import ru.spliterash.musicbox.customPlayers.playlist.SingletonPlayList;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.songContainers.types.SongContainer;
import ru.spliterash.musicbox.utils.BukkitUtils;

import java.util.Optional;

/**
 * Класс в котором находится вся инфа об игроке
 * Пока он на сервере
 */
@Getter
public class PlayerWrapper {
    public static final String METADATA_KEY = "musicboxInstance";
    /**
     * Игрок которому принадлежит этот инстанц
     */
    private final Player player;
    /**
     * Включен ли режим колонки
     * Не хочу хранить это в конфиге
     * Поскольку с этим может возникнуть много проблем
     */
    private boolean speaker;
    /**
     * Тихий режим
     * Игрок не слышит никого и ничего
     * <p>
     * Автоматически выключается при включении игроком какой-то музычки
     */
    @Setter
    private boolean silent = false;
    /**
     * Активный проигрыватель(если есть)
     */
    private PlayerSongPlayer activePlayer;

    /**
     * Боссбар для отображения проигрывания
     */
    @Getter(AccessLevel.NONE)
    private BossBar playBar;

    /**
     * Вызывается только если игрок захочет что нибудь послушать
     */
    private PlayerWrapper(Player player) {
        this.player = player;
        MusicBoxConfig.BossBarSetting bossBarConfig = MusicBox.getInstance().getConfigObject().getBossbar();
        if (bossBarConfig.isEnable()) {
            this.playBar = Bukkit.createBossBar("", BarColor.valueOf(bossBarConfig.getColor()), BarStyle.valueOf(bossBarConfig.getStyle()));
            this.playBar.setVisible(false);
            this.playBar.addPlayer(player);
        }
    }

    /**
     * Отдаёт не пустой {@link Optional} если игрок хоть раз включал музыку
     */
    public static Optional<PlayerWrapper> getInstanceOptional(Player player) {
        return Optional.ofNullable(BukkitUtils.extractMetadata(PlayerWrapper.class, player, METADATA_KEY));
    }

    /**
     * Создаёт инстанц если его нет и возращает
     */
    public static PlayerWrapper getInstance(Player player) {
        return getInstanceOptional(player)
                .orElseGet(() -> {
                    PlayerWrapper instance = new PlayerWrapper(player);
                    player.setMetadata(METADATA_KEY, new FixedMetadataValue(MusicBox.getInstance(), instance));
                    return instance;
                });
    }

    public static void clearAll() {
        Bukkit
                .getOnlinePlayers()
                .stream()
                .map(PlayerWrapper::getInstanceOptional)
                .forEach(o -> o.ifPresent(PlayerWrapper::destroy));
    }


    /**
     * Удаляет инстанц из игрока
     */
    public void destroy() {
        destroyActivePlayer();
        player.removeMetadata(METADATA_KEY, MusicBox.getInstance());
        if (playBar != null)
            playBar.removeAll();
    }

    public boolean isPlayNow() {
        return activePlayer != null;
    }

    /**
     * Может ли игрок поменять режим
     * А именно включить режим колонки
     */
    public boolean canSwitch() {
        return getPlayer().hasPermission("musicbox.speaker");
    }

    public boolean switchModeChecked() {
        if (!canSwitch()) {
            player.sendMessage(Lang.CANT_SWITCH.toString());
            return false;
        } else {
            switchMode();
            return true;
        }
    }

    /**
     * Меняет режим с колонки и обратно
     */
    public void switchMode() {
        speaker = !speaker;
        if (isPlayNow()) {
            PlayerSongPlayer oldPlayer = getActivePlayer();
            play(oldPlayer.getPlayList(), oldPlayer.getApiPlayer().getTick());
        }
    }

    public void play(IPlayList song) {
        play(song, (short) -1);
    }


    /**
     * Проигрывает игроку звук, с учётом его режима звучания
     * <p>
     * Внимание... Метод пересоздаёт текущий проигрыватель
     * для перемотки юзайте метод
     *
     * @param playList Поставщик следующей мелодии
     * @param tick     С какого тика (-1 если с начала)
     */
    public void play(IPlayList playList, short tick) {
        if (speaker)
            startSpeaker(playList);
        else
            startRadio(playList);
        if (tick > -1)
            activePlayer.getApiPlayer().setTick(tick);
    }

    public void startSpeaker(IPlayList playList) {
        destroyActivePlayer();
        activePlayer = new SpeakerPlayer(playList, this);
    }

    public void startRadio(IPlayList playList) {
        destroyActivePlayer();
        activePlayer = new RadioPlayer(playList, this);
    }

    /**
     * Полная остановка
     */
    public synchronized void destroyActivePlayer() {
        if (activePlayer != null) {
            activePlayer.destroy();
            afterDestroy();
        }
    }

    private void afterDestroy() {
        setBarVisible(false);
        activePlayer = null;
    }

    public void setBarVisible(boolean visible) {
        if (playBar != null)
            playBar.setVisible(visible);
    }

    public void setBarTitle(String title) {
        if (playBar != null)
            playBar.setTitle(title);
    }

    public void setBarProgress(double progress) {
        if (playBar != null)
            playBar.setProgress(progress);
    }

    public void play(MusicBoxSong song) {
        play(new SingletonPlayList(song));
    }

    public void play(SongContainer container) {
        play(ListPlaylist.fromContainer(container, false, false));
    }

    public void nullActivePlayer(MusicBoxSongPlayer playerModel) {
        if (activePlayer == playerModel) {
            afterDestroy();
        }
    }

    public boolean canHearMusic() {
        if (silent)
            return false;
        else if (!MusicBox.getInstance().getConfigObject().isHearPermissionsCheck())
            return true;
        else
            return player.hasPermission("musicbox.hear");
    }
}
