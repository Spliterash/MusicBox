package ru.spliterash.musicbox.minecraft.nms;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class OldNmsUtils {
    public Class<?> getNMSClass(String first, String nmsClassString) throws ClassNotFoundException {
        String name = first + "." + getOldNmsPackage() + "." + nmsClassString;
        return Class.forName(name);
    }

    public static String getOldNmsPackage() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
