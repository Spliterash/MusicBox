package ru.spliterash.musicbox.minecraft.nms.versionutils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;

@SuppressWarnings("deprecation")
public class OldVersion implements VersionUtils {

    @Override
    public BlockFace getRotation(Block block) {
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (data instanceof org.bukkit.material.Directional)
            return ((org.bukkit.material.Directional) data).getFacing();
        else
            return null;
    }

    @Override
    public void setRotation(Block block, BlockFace face) {
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (data instanceof org.bukkit.material.Directional) {
            ((Directional) data).setFacingDirection(face);
            state.setData(data);
            state.update(true, true);
        }
    }

    @Override
    public void setLever(Block block, boolean powered) {
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (data instanceof Lever) {
            Lever lever = (Lever) data;
            lever.setPowered(powered);
            state.setData(lever);
            state.update(true, true);
        }
    }
}
