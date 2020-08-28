package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import ru.spliterash.musicbox.MusicBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public void checkPrimary() {
        if (!Bukkit.isPrimaryThread())
            throw new RuntimeException("Call this only in primary thread");
    }
}
