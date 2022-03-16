package ru.spliterash.musicbox;

import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
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
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.spliterash.musicbox.customPlayers.abstracts.AbstractBlockPlayer;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.customPlayers.objects.SignPlayer;
import ru.spliterash.musicbox.customPlayers.objects.jukebox.JukeboxPlayer;
import ru.spliterash.musicbox.events.SourcedBlockRedstoneEvent;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.minecraft.nms.versionutils.VersionUtilsFactory;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.FaceUtils;
import ru.spliterash.musicbox.utils.RedstoneUtils;
import ru.spliterash.musicbox.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class Handler implements Listener {
    private static Consumer<ChunkUnloadEvent> chunkCanceller;

    static {
        try {
            //noinspection JavaReflectionMemberAccess
            Method method = ChunkUnloadEvent.class.getMethod("setCancelled", boolean.class);

            chunkCanceller = event -> {
                try {
                    method.invoke(event, true);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            chunkCanceller = event -> event.getChunk().load();
        }
    }

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

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent e) {
        @NotNull Chunk chunk = e.getChunk();
        Set<? extends AbstractBlockPlayer> playersInChunk = AbstractBlockPlayer.findByChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
        for (AbstractBlockPlayer player : playersInChunk) {
            if (player instanceof SignPlayer) {
                SignPlayer signPlayer = (SignPlayer) player;
                if (signPlayer.isPreventDestroy()) {
                    chunkCanceller.accept(e);
                    return;
                }
            }
            player.destroy();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRedstoneCB(SourcedBlockRedstoneEvent e) {
        BlockState state = e.getBlock().getState();
        if (state instanceof Sign) {
            Sign s = (Sign) state;
            if (SignPlayer.isPlayerSign(s)) {
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
        if (e.getHand() != EquipmentSlot.HAND)
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
            SignPlayer signPlayer = AbstractBlockPlayer.findByLocation(sign.getLocation());
            openControl(player, signPlayer);

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
