package ru.spliterash.musicbox;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.playlist.ListPlaylist;
import ru.spliterash.musicbox.customPlayers.playlist.SingletonPlayList;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.song.songContainers.SongContainer;
import ru.spliterash.musicbox.song.songContainers.containers.SingletonContainer;
import ru.spliterash.musicbox.utils.StringUtils;

import java.util.List;

public class Handler implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onExit(PlayerQuitEvent e) {
        PlayerWrapper
                .getInstanceOptional(e.getPlayer())
                .ifPresent(PlayerWrapper::destroy);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDie(PlayerDeathEvent e) {
        PlayerWrapper
                .getInstanceOptional(e.getEntity())
                .ifPresent(PlayerWrapper::destroyActivePlayer);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (e.getClickedBlock() == null)
            return;
        Block b = e.getClickedBlock();
        if (b.getState() instanceof Sign) {
            Sign sign = (Sign) b.getState();
            processSignClick(e.getPlayer(), sign);
        } else if (b.getState() instanceof Jukebox) {
            if (e.getHand() == EquipmentSlot.HAND)
                return;
            Jukebox jukebox = (Jukebox) b.getState();
            @Nullable ItemStack item = e.getItem();
            processJukeboxClick(e.getPlayer(), jukebox, item, e);
        }
    }

    private void processJukeboxClick(Player player, Jukebox jukebox, ItemStack item, Cancellable event) {

    }

    private void processSignClick(Player player, Sign sign) {
        if (!player.hasPermission("musicbox.sign")) {
            player.sendMessage(Lang.NO_PEX.toString());
            return;
        }
        String lineTwo = sign.getLine(1);
        if (!StringUtils.strip(lineTwo).equalsIgnoreCase("[music]"))
            return;
        String songId = sign.getLine(0);
        // Терь если табличка не настроена
        if (songId.isEmpty()) {
            GUIActions.openSignSetupInventory(PlayerWrapper.getInstance(player), sign);
        } else if (songId.startsWith(ChatColor.AQUA.toString())) {
            boolean rand = sign.getLine(3).contains("RAND");
            MusicBoxSongManager
                    .getContainerById(StringUtils.strip(songId))
                    .ifPresent(c -> {
                        List<MusicBoxSong> list = c.getSongsRand(rand);
                        IPlayList container;
                        if (list.size() == 1)
                            container = new SingletonPlayList(list.get(0));
                        else
                            container = new ListPlaylist(list);
                        PlayerWrapper.getInstance(player).play(container);
                    });
        }
    }

}
