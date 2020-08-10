package ru.spliterash.musicbox.players;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.utils.BukkitUtils;

/**
 * Класс в котором находится вся инфа об игроке
 * Пока он на сервере
 */
@Getter
public class PlayerInstance {
    public static final String METADATA_KEY = "musicboxInstance";
    private final Player player;

    private PlayerInstance(Player player) {
        this.player = player;
    }

    public PlayerInstance getInstance(Player player) {
        PlayerInstance obj = BukkitUtils.getMobMeta(PlayerInstance.class, player, METADATA_KEY);
        if (obj == null) {
            obj = new PlayerInstance(player);
            player.setMetadata(METADATA_KEY, new FixedMetadataValue(MusicBox.getInstance(), obj));
        }
        return obj;
    }
}
