package ru.spliterash.musicbox;

import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
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

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR))
            return;
        if (e.getClickedBlock() == null)
            return;
        if (e.getHand() == EquipmentSlot.HAND)
            return;
        Block b = e.getClickedBlock();
        if (b.getState() instanceof Sign) {
            Sign sign = (Sign) b.getState();
            processSignClick(e.getPlayer(), sign);
        } else if (b.getState() instanceof Jukebox) {
            Jukebox jukebox = (Jukebox) b.getState();
            @Nullable ItemStack item = e.getItem();
            processJukeboxClick(e.getPlayer(), jukebox, item);
        }
    }

    private void processJukeboxClick(Player player, Jukebox jukebox, ItemStack item) {
        // TODO
    }

    private void processSignClick(Player player, Sign sign) {
        String lineTwo = sign.getLine(1);
        if (!StringUtils.strip(lineTwo).equalsIgnoreCase("[music]"))
            return;
        String songId = StringUtils.strip(sign.getLine(0));
        // Терь если табличка не настроена
        if (songId.isEmpty()) {
            GUIActions.openSignSetupInventory(PlayerWrapper.getInstance(player), sign);
        }
    }

}
