package ru.spliterash.musicbox.minecraft.nms.versionutils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rotatable;

public class NewVersion implements VersionUtils {
    @Override
    public BlockFace getRotation(Block block) {
        MyRotate rotate = MyRotate.by(block.getBlockData());

        if (rotate == null)
            return BlockFace.SELF;
        return rotate.getRotate();

    }

    @Override
    public void setRotation(Block block, BlockFace face) {
        MyRotate rotate = MyRotate.by(block.getBlockData());

        if (rotate == null)
            return;

        rotate.setRotate(face);
        block.setBlockData(rotate.getData(), false);
    }

    @Override
    public void setLever(Block block, boolean powered) {
        if (block.getBlockData() instanceof Powerable) {
            Powerable data = (Powerable) block.getBlockData();
            data.setPowered(powered);
            block.setBlockData(data, true);
        }
    }

    private interface MyRotate {
        void setRotate(BlockFace rotate);

        BlockFace getRotate();

        static MyRotate by(BlockData data) {
            if (data instanceof Directional)
                return new BlockFaceRotate((Directional) data);
            else if (data instanceof Rotatable)
                return new RotatableRotate((Rotatable) data);
            else
                return null;
        }

        BlockData getData();
    }

    private static class BlockFaceRotate implements MyRotate {

        private final Directional directional;

        private BlockFaceRotate(Directional directional) {
            this.directional = directional;
        }

        @Override
        public void setRotate(BlockFace rotate) {
            directional.setFacing(rotate);
        }

        @Override
        public BlockFace getRotate() {
            return directional.getFacing();
        }

        @Override
        public BlockData getData() {
            return directional;
        }
    }

    private static class RotatableRotate implements MyRotate {
        private final Rotatable rotatable;

        private RotatableRotate(Rotatable rotatable) {
            this.rotatable = rotatable;
        }

        @Override
        public void setRotate(BlockFace rotate) {
            this.rotatable.setRotation(rotate);
        }

        @Override
        public BlockFace getRotate() {
            return this.rotatable.getRotation();
        }

        @Override
        public BlockData getData() {
            return rotatable;
        }
    }
}
