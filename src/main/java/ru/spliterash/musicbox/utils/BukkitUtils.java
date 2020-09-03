package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.MusicBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class BukkitUtils {
    public final List<XMaterial> DISCS = Collections.unmodifiableList(
            Arrays.asList(
                    XMaterial.MUSIC_DISC_13,
                    XMaterial.MUSIC_DISC_BLOCKS,
                    XMaterial.MUSIC_DISC_CAT,
                    XMaterial.MUSIC_DISC_CHIRP,
                    XMaterial.MUSIC_DISC_FAR,
                    XMaterial.MUSIC_DISC_MALL,
                    XMaterial.MUSIC_DISC_MELLOHI,
                    XMaterial.MUSIC_DISC_STAL,
                    XMaterial.MUSIC_DISC_STRAD,
                    XMaterial.MUSIC_DISC_WAIT,
                    XMaterial.MUSIC_DISC_WARD
            )
    );

    public XMaterial getRandomDisc() {
        return ArrayUtils.getRandom(DISCS);
    }

    public void runSyncTask(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(MusicBox.getInstance(), runnable);
    }

    public <T> T extractMetadata(Class<T> metaType, Metadatable metadatable, String key) {
        List<MetadataValue> meta = metadatable.getMetadata(key);
        for (MetadataValue value : meta) {
            Object valueObj = value.value();
            try {
                return metaType.cast(valueObj);
            } catch (ClassCastException ignored) {
            }
        }
        return null;
    }

    public Location centerBlock(Location location) {
        return new Location(
                location.getWorld(),
                location.getBlockX() + 0.5,
                location.getBlockY() + 0.5,
                location.getBlockZ() + 0.5
        );
    }

    /**
     * Если у игрока открыт инвентарь, то что с ним делать
     *
     * @param holder Холдер инвентаря
     * @return Стрим с игроками у которых открыт этот инвентарь
     */
    public Set<Player> findOpenPlayers(InventoryHolder holder) {
        checkPrimary();
        return Bukkit
                .getOnlinePlayers()
                .stream()
                .filter(p -> {
                    Inventory inv = p.getOpenInventory().getTopInventory();
                    @Nullable InventoryHolder cHolder = inv.getHolder();
                    return holder.equals(cHolder);
                })
                .collect(Collectors.toSet());
    }

    public void checkPrimary() {
        if (!Bukkit.isPrimaryThread())
            throw new RuntimeException("Call this only in primary thread");
    }
}
