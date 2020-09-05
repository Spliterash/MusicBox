package ru.spliterash.musicbox;

import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.customPlayers.abstracts.AbstractBlockPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.customPlayers.objects.SignPlayer;
import ru.spliterash.musicbox.customPlayers.objects.jukebox.JukeboxPlayer;
import ru.spliterash.musicbox.events.SourcedBlockRedstoneEvent;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.minecraft.nms.versionutils.VersionUtilsFactory;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.*;

import java.util.Optional;

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

    @EventHandler
    public void onEditEnd(SignChangeEvent e) {
        String secondLine = e.getLine(1);
        if (secondLine == null || !secondLine.equalsIgnoreCase("[music]"))
            return;
        BlockFace face = VersionUtilsFactory.getInstance().getRotation(e.getBlock());
        if (FaceUtils.isValidFace(face))
            return;
        e.getPlayer().sendMessage(Lang.WRONG_SIGN_FACE.toString());
        VersionUtilsFactory.getInstance().setRotation(e.getBlock(), FaceUtils.normalizeFace(face));

    }

    @EventHandler(ignoreCancelled = true)
    public void onSongEnd(SongEndEvent e) {
        if (e.getSongPlayer() instanceof MusicBoxSongPlayer) {
            ((MusicBoxSongPlayer) e.getSongPlayer()).onSongEnd();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRedstone(BlockRedstoneEvent e) {
        if (!MusicBox.getInstance().isLoaded()) {
            return;
        }
        RedstoneUtils.handleRedstoneForBlock(e.getBlock(), e.getOldCurrent(), e.getNewCurrent());
    }

    @EventHandler
    public void onRedstoneCB(SourcedBlockRedstoneEvent e) {
        BlockState state = e.getBlock().getState();
        if (state instanceof Sign) {
            Sign s = (Sign) state;
            if (s.getLine(1).equals(SignPlayer.SIGN_SECOND_LINE)) {
                int pin = RedstoneUtils.getPin(s.getBlock(), e.getSource());
                SignPlayer.redstoneSign(s, pin, e.getNewCurrent());
            }
        } else if (state instanceof Jukebox) {
            Jukebox box = (Jukebox) state;
            JukeboxPlayer.onRedstone(box, e.getSource(), e.getNewCurrent());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (!MusicBox.getInstance().isLoaded()) {
            return;
        }
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (e.getClickedBlock() == null)
            return;
        Block b = e.getClickedBlock();
        if (b.getState() instanceof Sign) {
            Sign sign = (Sign) b.getState();
            processSignClick(e.getPlayer(), sign);
        } else if (b.getState() instanceof Jukebox) {
            Jukebox jukebox = (Jukebox) b.getState();
            ItemStack item = e.getItem();
            if (e.getPlayer().isSneaking()) {
                if (item == null) {
                    JukeboxPlayer.onSneakingClick(jukebox, e.getPlayer());
                }
            } else {
                jukebox.eject();
                JukeboxPlayer.onJukeboxClick(jukebox, item, e);
            }
        }
    }

    private void processSignClick(Player player, Sign sign) {
        Optional<AbstractBlockPlayer> infoSign = AbstractBlockPlayer
                .findByInfoSign(sign.getLocation());
        infoSign.ifPresent(a -> openControl(player, a));

        String lineTwo = sign.getLine(1);
        if (!StringUtils.strip(lineTwo).equalsIgnoreCase("[music]"))
            return;
        String songId = sign.getLine(0);
        // Терь если табличка не настроена
        if (songId.isEmpty()) {
            if (player.hasPermission("musicbox.sign")) {
                GUIActions.openSignSetupInventory(PlayerWrapper.getInstance(player), sign);
            } else {
                player.sendMessage(Lang.NO_PEX.toString());
            }
        } else if (songId.startsWith(ChatColor.AQUA.toString())) {
            ItemStack item = player.getInventory().getItemInMainHand();
            // Если игрок шифтит и в руке ничего нет
            //noinspection ConstantConditions
            if (player.isSneaking() && (item == null || item.getType() == Material.AIR)) {
                SignPlayer signPlayer = AbstractBlockPlayer.findByLocation(sign.getLocation());
                openControl(player, signPlayer);
            } else {
                SignUtils
                        .parseSignPlaylist(sign)
                        .ifPresent(p -> PlayerWrapper.getInstance(player).play(p));
            }
        }
    }

    private void openControl(Player player, AbstractBlockPlayer blockPlayer) {
        if (blockPlayer != null) {
            blockPlayer.getControl().open(player);
        } else {
            player.sendMessage(Lang.BLOCK_NOT_PLAY.toString());
        }
    }

}
