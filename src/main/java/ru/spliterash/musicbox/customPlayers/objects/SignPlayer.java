package ru.spliterash.musicbox.customPlayers.objects;

import com.cryptomorin.xseries.XMaterial;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.PositionPlayer;
import ru.spliterash.musicbox.customPlayers.models.MusicBoxSongPlayerModel;
import ru.spliterash.musicbox.customPlayers.models.RangePlayerModel;
import ru.spliterash.musicbox.minecraft.nms.block.VersionUtils;
import ru.spliterash.musicbox.minecraft.nms.block.VersionUtilsFactory;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.FaceUtils;
import ru.spliterash.musicbox.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class SignPlayer extends PositionSongPlayer implements PositionPlayer {
    public static final String SIGN_SECOND_LINE = String.format("%s[%sMUSIC%s]", ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY);
    private final static Map<Location, SignPlayer> players = new HashMap<>();
    private final MusicBoxSongPlayerModel musicBoxModel;
    private final RangePlayerModel rangePlayerModel;
    private final BukkitTask task;
    private Location infoSign;

    /**
     * Создаёт новый проигрыватель для таблички
     *
     * @param list Плейлист который к ней привязан
     * @param sign Табличка к которой привязана эта музыка
     */
    private SignPlayer(IPlayList list, int range, Sign sign) {
        super(list.getCurrent().getSong());
        Location location = sign.getLocation();
        setRange(range);
        setTargetLocation(BukkitUtils.centerBlock(location));
        SignPlayer oldBlock = players.put(getTargetLocation(), this);
        if (oldBlock != null)
            oldBlock.destroy();
        this.musicBoxModel = new MusicBoxSongPlayerModel(this, list, l -> new SignPlayer(l, range, sign));
        this.rangePlayerModel = new RangePlayerModel(musicBoxModel);
        musicBoxModel.runPlayer();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                while (!isDestroyed()) {
                    rangePlayerModel.tick();
                    checkSign();
                    try {
                        //noinspection BusyWait
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }.runTaskAsynchronously(MusicBox.getInstance());
        if (sign.getLine(3).contains("I"))
            SignUtils
                    .findSign(sign.getLocation())
                    .ifPresent(s -> {
                        infoSign = s.getLocation();
                        SignUtils.setPlayListInfo(infoSign, list);
                    });
    }

    public static Optional<SignPlayer> findByInfoSign(Location location) {
        return players
                .values()
                .stream()
                .filter(i -> i.infoSign != null && i.infoSign.equals(location))
                .findFirst();
    }

    public void checkSign() {
        BukkitUtils.runSyncTask(() -> {
            Block b = getTargetLocation().getBlock();
            if (!(b.getState() instanceof Sign) || !b.isBlockIndirectlyPowered())
                destroy();
        });
    }

    public static Optional<SignPlayer> getPlayer(Location location) {
        location = BukkitUtils.centerBlock(location);
        return Optional.ofNullable(players.get(location));
    }

    /**
     * Пихать только таблички плугина
     *
     * @param sign       Табличка
     * @param pin        какой стороны был редстоун
     * @param newCurrent Мощность
     */
    public static void redstoneSign(Sign sign, int pin, int newCurrent) {
        SignPlayer player = players.get(BukkitUtils.centerBlock(sign.getLocation()));
        // Связано с включением или выключением
        if (pin == 0) {
            if (newCurrent > 0) {
                int range = SignUtils.parseSignRange(sign);
                SignUtils.parseSignPlaylist(sign)
                        .ifPresent(l -> new SignPlayer(l, range, sign));
            } else if (player != null) {
                player.destroy();
            }
        } else if (player != null && newCurrent > 0) {
            IPlayList list = player.getPlayList();
            switch (pin) {
                case 1:
                    list.back(1);
                    break;
                case 2:
                    list.next();
                    break;
                default:
                    return;
            }
            new SignPlayer(list, player.getRange(), sign);
        }
    }

    @Override
    public void destroy() {
        if (!isDestroyed()) {

            super.destroy();
            players.values().remove(this);
            boolean normalEnd = musicBoxModel.isSongEndNormal();
            if (normalEnd)
                pingLever();

            if (infoSign != null) {
                BukkitUtils.runSyncTask(() -> {
                    if (!musicBoxModel.getPlayList().hasNext())
                        SignUtils.setPlayerOff(infoSign);
                    else if (!normalEnd)
                        SignUtils.setPlayerOff(infoSign);
                });
            }

            rangePlayerModel.destroy();
            musicBoxModel.destroy();

        }
    }

    private void pingLever() {
        BukkitUtils.runSyncTask(() -> {
            @NotNull Block block = getTargetLocation().getBlock();
            BlockFace face = VersionUtilsFactory.getInstance().getRotation(block);
            face = FaceUtils.invertFace(face);
            @NotNull Block leverBlock = block.getRelative(face, 2);

            if (XMaterial.matchXMaterial(leverBlock.getType()) == XMaterial.LEVER) {
                VersionUtils utils = VersionUtilsFactory.getInstance();
                utils.setLever(leverBlock, true);
                Bukkit.getScheduler().runTaskLater(MusicBox.getInstance(), () -> {
                    utils.setLever(leverBlock, false);
                }, 10);
            }
        });
    }


    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public Location getLocation() {
        return getTargetLocation();
    }

    @Override
    public int getRange() {
        return getDistance();
    }

    @Override
    public void setRange(int range) {
        setDistance(range);
    }
}
