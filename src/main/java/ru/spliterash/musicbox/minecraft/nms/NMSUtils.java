package ru.spliterash.musicbox.minecraft.nms;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class NMSUtils {
    @Getter
    private String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    public Class<?> getNMSClass(String first, String nmsClassString) throws ClassNotFoundException {
        String name = first + "." + version + "." + nmsClassString;
        return Class.forName(name);
    }

}
