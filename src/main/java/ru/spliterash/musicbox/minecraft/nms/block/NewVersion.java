package ru.spliterash.musicbox.minecraft.nms.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;

public class NewVersion implements VersionUtils {
    @Override
    public BlockFace getRotation(Block block) {

        if (!(block.getBlockData() instanceof Directional)) return BlockFace.SELF;
        Directional direction = (Directional) block.getBlockData();
        return direction.getFacing();

    }

    @Override
    public void setRotation(Block block, BlockFace face) {
        if (!(block.getBlockData() instanceof Directional)) return;
        Directional direction = (Directional) block.getBlockData();
        direction.setFacing(face);
        block.setBlockData(direction, false);
    }

    @Override
    public void setLever(Block block, boolean powered) {
        if (block.getBlockData() instanceof Powerable) {
            Powerable data = (Powerable) block.getBlockData();
            data.setPowered(powered);
            block.setBlockData(data, false);
        }
    }
}
