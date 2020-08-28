package ru.spliterash.musicbox;

import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.customPlayers.objects.SignPlayer;
import ru.spliterash.musicbox.events.SourcedBlockRedstoneEvent;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.minecraft.nms.block.VersionUtilsFactory;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.FaceUtils;
import ru.spliterash.musicbox.utils.RedstoneUtils;
import ru.spliterash.musicbox.utils.SignUtils;
import ru.spliterash.musicbox.utils.StringUtils;

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
        RedstoneUtils.handleRedstoneForBlock(e.getBlock(), e.getOldCurrent(), e.getNewCurrent());
    }

    @EventHandler
    public void onRedstoneCB(SourcedBlockRedstoneEvent e) {
        if (e.getBlock().getState() instanceof Sign) {
            Sign s = (Sign) e.getBlock().getState();
            if (s.getLine(1).equals(SignPlayer.SIGN_SECOND_LINE)) {
                int pin = RedstoneUtils.getPin(s.getBlock(), e.getSource());
                SignPlayer.redstoneSign(s, pin, e.getNewCurrent());
            }
        }
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
            if (player.isSneaking()) {
                //noinspection ConstantConditions
                if ((item == null || item.getType() == Material.AIR)) {
                    SignPlayer signPlayer = SignPlayer.getPlayer(sign.getLocation()).orElse(null);
                    if (signPlayer != null) {
                        signPlayer.getRewind().openForPlayer(player);
                    } else {
                        player.sendMessage(Lang.BLOCK_NOT_PLAY.toString());
                    }
                }
            } else
                SignUtils
                        .parseSignPlaylist(sign, true)
                        .ifPresent(p -> PlayerWrapper.getInstance(player).play(p));
        }
    }

}
