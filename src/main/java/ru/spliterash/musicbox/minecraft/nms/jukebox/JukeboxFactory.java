package ru.spliterash.musicbox.minecraft.nms.jukebox;

import org.bukkit.block.Jukebox;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

import java.lang.reflect.InvocationTargetException;

public class JukeboxFactory {
    private static Class<? extends JukeboxCustom> clazz;
    private static final boolean enable;

    static {
        String v = NMSUtils.getVersion();
        String str = v.substring(0, v.lastIndexOf('_'));
        int iV = Integer.parseInt(str.split("_")[1]);
        if (iV >= 13) {
            clazz = NewVersion.class;
            enable = true;
        } else if (iV == 12) {
            clazz = v1_12.class;
            enable = true;
        } else
            enable = false;
    }

    public static boolean isEnable() {
        return enable;
    }

    public static JukeboxCustom getJukebox(Jukebox jukebox) {
        if (!isEnable())
            return null;
        try {
            return clazz.getDeclaredConstructor(Jukebox.class).newInstance(jukebox);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
