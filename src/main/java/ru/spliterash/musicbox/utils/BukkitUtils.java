package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import ru.spliterash.musicbox.MusicBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class BukkitUtils {
    public final List<XMaterial> DISCS = Collections.unmodifiableList(
            Arrays.asList(
                    XMaterial.MUSIC_DISC_11,
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

    public void runSyncTask(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(MusicBox.getInstance(), runnable);
    }
}
