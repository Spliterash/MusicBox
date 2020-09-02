package ru.spliterash.musicbox.minecraft.nms.versionutils;

import lombok.Getter;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

public class VersionUtilsFactory {
    @Getter
    private static final VersionUtils instance;

    static {
        String v = NMSUtils.getVersion();
        String str = v.substring(0, v.lastIndexOf('_'));
        int iV = Integer.parseInt(str.split("_")[1]);
        if (iV >= 13) {
            instance = new NewVersion();
        } else if (iV == 12) {
            instance = new v1_12();
        } else {
            instance = null;
        }
    }

}
