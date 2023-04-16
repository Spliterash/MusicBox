package ru.spliterash.musicbox.minecraft.nms.versionutils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public interface VersionUtils {

    BlockFace getRotation(Block block);

    void setRotation(Block block, BlockFace face);

    void setLever(Block block, boolean powered);
}
