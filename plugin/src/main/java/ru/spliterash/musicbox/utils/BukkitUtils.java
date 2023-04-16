package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.MusicBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        else if (MusicBox.getInstance().isEnabled())
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

    public static String locationToString(Location location) {
        return String.format("%s|%s|%s|%s", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static Location parseLocation(String string) {
        String[] split = string.split("\\|");
        if (split.length != 4)
            return null;
        World world = Bukkit.getWorld(split[0]);
        if (world == null)
            return null;
        double[] array = new double[3];
        for (int i = 0; i < 3; i++) {
            double d;
            try {
                d = Double.parseDouble(split[i + 1]);
                array[i] = d;
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return new Location(world, array[0], array[1], array[2]);
    }

    /**
     * Проверяет принадлежит ли данная локация к этому чанку
     */
    public static boolean inChunk(Location location, World chunkWorld, int chunkX, int chunkZ) {
        if (!location.getWorld().equals(chunkWorld))
            return false;
        int xp = chunkX * 16; // must multiple by 16 to get the blocks location
        int zp = chunkZ * 16; // must multiple by 16 to get the blocks location
        int x = -9314;
        int z = -931;
        return (xp <= x) && (xp + 15 >= x) && (zp <= z) && (zp + 15 >= z);

    }
}
