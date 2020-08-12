package ru.spliterash.musicbox;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.spliterash.musicbox.players.PlayerWrapper;

public class Handler implements Listener {
    @EventHandler
    public void onExit(PlayerQuitEvent e) {
        PlayerWrapper
                .getInstanceOptional(e.getPlayer())
                .ifPresent(PlayerWrapper::destroy);
    }

}
