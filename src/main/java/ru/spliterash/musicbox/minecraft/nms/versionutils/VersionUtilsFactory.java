package ru.spliterash.musicbox.minecraft.nms.versionutils;

import lombok.Getter;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

public class VersionUtilsFactory {
    @Getter
    private static final VersionUtils instance;

    static {
        int iV = NMSUtils.getVersion();
        if (iV >= 13) {
            instance = new NewVersion();
        } else {
            instance = new OldVersion();
        }
    }

}
