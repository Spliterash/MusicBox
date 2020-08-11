package ru.spliterash.musicbox.players;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.MusicBoxConfig;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.objects.RadioPlayer;
import ru.spliterash.musicbox.customPlayers.objects.SpeakerPlayer;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.song.SongContainerGUI;
import ru.spliterash.musicbox.utils.BukkitUtils;

import java.io.File;
import java.util.Optional;

/**
 * Класс в котором находится вся инфа об игроке
 * Пока он на сервере
 */
@Getter
public class PlayerInstance {
    public static final String METADATA_KEY = "musicboxInstance";
    private static final File playersFolder = new File(MusicBox.getInstance().getDataFolder(), "players");
    /**
     * Игрок которому принадлежит этот инстанц
     */
    private final Player player;
    private final PlayerConfig config;

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
    private PlayerInstance(Player player) {
        this.player = player;
        MusicBoxConfig.BossBarSetting bossBarConfig = MusicBox.getInstance().getConfigObject().getBossbar();
        if (bossBarConfig.isEnable())
            this.playBar = Bukkit.createBossBar("", BarColor.valueOf(bossBarConfig.getColor()), BarStyle.valueOf(bossBarConfig.getStyle()));
        this.config = PlayerConfig.load(new File(playersFolder, player.getUniqueId() + ".yml"));
    }

    /**
     * Отдаёт не пустой {@link Optional} если игрок хоть раз включал музыку
     */
    public static Optional<PlayerInstance> getInstanceOptional(Player player) {
        return Optional.ofNullable(BukkitUtils.extractMetadata(PlayerInstance.class, player, METADATA_KEY));
    }

    /**
     * Создаёт инстанц если его нет и возращает
     */
    public static PlayerInstance getInstance(Player player) {
        return getInstanceOptional(player)
                .orElseGet(() -> {
                    PlayerInstance instance = new PlayerInstance(player);
                    player.setMetadata(METADATA_KEY, new FixedMetadataValue(MusicBox.getInstance(), instance));
                    return instance;
                });
    }

    public static void clearAll() {
        Bukkit
                .getOnlinePlayers()
                .stream()
                .map(PlayerInstance::getInstanceOptional)
                .forEach(o -> o.ifPresent(PlayerInstance::destroy));
    }

    /**
     * Открыть инвентарь для выбора музыки
     */
    public void openPlayInventory() {
        SongContainerGUI gui = MusicBoxSongManager.getRootContainer().createGUI(player);
        gui.openPage(
                0,
                true,
                GUIMode::addMusicToPlaylist,
                GUIMode::playerPlayMusic,
                GUIMode::addToPlaylist,
                GUIMode::addContainerToPlaylist,
                GUIMode::addToPlaylist /*TODO написать плейлисты */
        );
    }


    /**
     * Сохраняет всё на жесткий диск
     * И удаляет инстанц из игрока
     */
    public void destroy() {
        destroyActivePlayer();
        player.removeMetadata(METADATA_KEY, MusicBox.getInstance());
        playBar.removeAll();
        config.save();
    }

    public void play(MusicBoxSong song) {
        if (config.isSpeaker())
            startSpeaker(song);
        else
            startRadio(song);
    }

    public void startSpeaker(MusicBoxSong song) {
        destroyActivePlayer();
        activePlayer = new SpeakerPlayer(song, this);
        //TODO Режим колонки
    }

    public void startRadio(MusicBoxSong song) {
        destroyActivePlayer();
        activePlayer = new RadioPlayer(song, this);
    }

    /**
     * Останавливает активный проигрыватель
     */
    public synchronized void destroyActivePlayer() {
        if (activePlayer != null) {
            activePlayer.destroy();
            activePlayer = null;
        }
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
}
