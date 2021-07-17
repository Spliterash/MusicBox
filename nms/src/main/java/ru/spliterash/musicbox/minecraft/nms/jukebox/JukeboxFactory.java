package ru.spliterash.musicbox.minecraft.nms.jukebox;

import org.bukkit.block.Jukebox;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;
import ru.spliterash.musicbox.minecraft.nms.jukebox.versions.V12;
import ru.spliterash.musicbox.minecraft.nms.jukebox.versions.V13_16;
import ru.spliterash.musicbox.minecraft.nms.jukebox.versions.V17;

import java.lang.reflect.InvocationTargetException;

public class JukeboxFactory {
    private static final Class<? extends IJukebox> clazz;

    static {
        int iV = NMSUtils.getVersion();
        if (iV >= 17)
            clazz = V17.class;
        else if (iV >= 13)
            clazz = V13_16.class;
        else
            clazz = V12.class;
    }

    public static IJukebox getJukebox(Jukebox jukebox) {
        try {
            return clazz.getDeclaredConstructor(Jukebox.class).newInstance(jukebox);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
