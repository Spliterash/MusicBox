package ru.spliterash.musicbox.customPlayers.objects.jukebox;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.customPlayers.abstracts.AbstractBlockPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.minecraft.nms.jukebox.JukeboxFactory;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.SignUtils;

@Getter
public class JukeboxPlayer extends AbstractBlockPlayer {
    private Location infoSign;

    private JukeboxPlayer(IPlayList list, int range, Jukebox box) {
        super(list, box.getLocation(), range);
        SignUtils
                .findSign(box.getLocation())
                .ifPresent(s -> {
                    infoSign = s.getLocation();
                    SignUtils.setPlayListInfo(infoSign, list);
                });
    }

    public static void onJukeboxClick(Jukebox jukebox, ItemStack clickedItem, PlayerInteractEvent e) {
        JukeboxPlayer sp = AbstractBlockPlayer.findByLocation(jukebox.getLocation());
        if (clickedItem == null && sp == null)
            return;
        if (sp != null) {
            sp.destroy();
        }
        if (clickedItem == null)
            return;
        MusicBoxSong song = MusicBoxSongManager.findByItem(clickedItem).orElse(null);
        if (song == null) {
            if (MusicBoxSongManager.tryReplaceLegacyItem(e.getPlayer(), clickedItem))
                e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        e.getPlayer().getInventory().setItemInMainHand(null);
        JukeboxFactory.getJukebox(jukebox).setJukebox(clickedItem);

        createNew(jukebox);
    }

    private static void createNew(Jukebox jukebox) {
        try {
            JukeboxPlaylistImpl playlist = new JukeboxPlaylistImpl(jukebox.getLocation());
            new JukeboxPlayer(playlist, MusicBox.getInstance().getConfigObject().getJukeboxRadius(), jukebox);
        } catch (JukeboxPlaylistInitException e) {
            // NOTHING
        }
    }

    /**
     * Вызывается когда игрок кликает с зажатым шифтом
     */
    public static void onSneakingClick(Jukebox jukebox, Player player) {
        JukeboxPlayer songPlayer = AbstractBlockPlayer.findByLocation(jukebox.getLocation());
        if (songPlayer != null) {
            songPlayer.getControl().open(player);
        }
    }

    /**
     * Вызывается когда редстоун тычет
     *
     * @param box    Проигрыватель
     * @param source Откуда пришёл сигнал
     * @param power  Сила сигнала
     */
    public static void onRedstone(Jukebox box, Block source, int power) {
        if (power > 0) {
            JukeboxPlayer player = AbstractBlockPlayer.findByLocation(box.getLocation());
            if (player != null) {
                player.getMusicBoxModel().startNext();
            } else
                createNew(box);
        }
    }

    @Override
    protected void every100MillisAsync() {
        BukkitUtils.runSyncTask(() -> {
            Block b = getTargetLocation().getBlock();
            if (!(b.getState() instanceof Jukebox))
                destroy();
        });
    }

    @Override
    protected JukeboxPlayer runNextSong(IPlayList list) {
        @NotNull BlockState state = getTargetLocation().getBlock().getState();
        if (state instanceof Jukebox) {
            return new JukeboxPlayer(list, getRange(), (Jukebox) state);
        } else
            return null;
    }

    @Override
    protected void songEnd() {
        // NOTHING
    }
}
