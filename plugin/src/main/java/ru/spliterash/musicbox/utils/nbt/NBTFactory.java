package ru.spliterash.musicbox.utils.nbt;

import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

public class NBTFactory {
    public static final NBTHandler NBT_HANDLER;

    static {
        String raw = NMSUtils.getRawVersion();
        int iV = NMSUtils.parseMajorVersion(raw);

        if (iV >= 13) {
            NBT_HANDLER = new BukkitNbtHandler();
        } else {
            NBT_HANDLER = new NbtEditorHandler();
        }
    }

}
