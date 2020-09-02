package ru.spliterash.musicbox.minecraft.nms.jukebox;

import org.bukkit.block.Jukebox;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

import java.lang.reflect.InvocationTargetException;

public class JukeboxFactory {
    private static Class<? extends JukeboxCustom> clazz;

    static {
        String v = NMSUtils.getVersion();
        String str = v.substring(0, v.lastIndexOf('_'));
        int iV = Integer.parseInt(str.split("_")[1]);
        if (iV >= 13) {
            clazz = NewVersion.class;
        } else if (iV == 12) {
            clazz = v1_12.class;
        }
    }

    public static JukeboxCustom getJukebox(Jukebox jukebox) {
        try {
            return clazz.getDeclaredConstructor(Jukebox.class).newInstance(jukebox);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
