package ru.spliterash.musicbox.minecraft.nms.jukebox;

import org.bukkit.block.Jukebox;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

import java.lang.reflect.InvocationTargetException;

public class JukeboxFactory {
    private static final String START_PATH = "ru.spliterash.musicbox.minecraft.nms.jukebox.versions.";
    private static final Class<? extends IJukebox> clazz;

    static {
        String raw = NMSUtils.getRawVersion();
        int iV = NMSUtils.parseMajorVersion(raw);

        String className;
        if (iV == 21) {
            switch (raw) {
                case "1.21":
                case "1.21.1":
                    className = START_PATH + "V21";
                    break;
                case "1.21.2":
                case "1.21.3":
                    className = START_PATH + "V21_2";
                    break;
                default:
                    className = null;
                    break;
            }
        } else if (iV == 20) {
            switch (raw) {
                case "1.20":
                case "1.20.1":
                    className = START_PATH + "V20_1";
                    break;
                case "1.20.2":
                    className = START_PATH + "V20_2";
                    break;
                case "1.20.3":
                case "1.20.4":
                    className = START_PATH + "V20_3";
                    break;
                case "1.20.5":
                case "1.20.6":
                default:
                    className = START_PATH + "V20_5";
                    break;
            }
        } else if (iV == 19) {
            switch (raw) {
                case "1.19.2":
                    className = START_PATH + "V19_2";
                    break;
                case "1.19.3":
                    className = START_PATH + "V19_3";
                    break;
                case "1.19.4":
                    className = START_PATH + "V19_4";
                    break;
                default:
                    className = null;
                    break;
            }
        } else if (iV == 18)
            className = START_PATH + "V18";
        else if (iV == 17)
            className = START_PATH + "V17";
        else if (iV >= 13)
            className = START_PATH + "V13_16";
        else if (iV == 12)
            className = START_PATH + "V12";
        else
            className = null;

        if (className == null)
            throw new IllegalArgumentException("Unsupported version: " + raw);
        Class<? extends IJukebox> tmpClass = null;
        try {
            //noinspection unchecked
            tmpClass = (Class<? extends IJukebox>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            clazz = tmpClass;
        }
    }

    public static boolean jukeboxAvailable() {
        return clazz != null;
    }

    public static IJukebox getJukebox(Jukebox jukebox) {
        try {
            return clazz.getConstructor(Jukebox.class).newInstance(jukebox);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
