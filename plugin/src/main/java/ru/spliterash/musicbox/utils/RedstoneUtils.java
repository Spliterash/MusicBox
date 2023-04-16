package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import ru.spliterash.musicbox.events.SourcedBlockRedstoneEvent;
import ru.spliterash.musicbox.minecraft.nms.versionutils.VersionUtilsFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Честно спи****о у CraftBook'а
 */
@SuppressWarnings({"unused"})
@UtilityClass
public class RedstoneUtils {
    private final Set<XMaterial> isRedstoneBlock = new HashSet<>();

    static {
        isRedstoneBlock.add(XMaterial.POWERED_RAIL);
        isRedstoneBlock.add(XMaterial.DETECTOR_RAIL);
        isRedstoneBlock.add(XMaterial.STICKY_PISTON);
        isRedstoneBlock.add(XMaterial.PISTON);
        isRedstoneBlock.add(XMaterial.LEVER);
        isRedstoneBlock.add(XMaterial.STONE_PRESSURE_PLATE);
        isRedstoneBlock.addAll(ItemUtils.getEndWith("_PRESSURE_PLATE"));
        isRedstoneBlock.add(XMaterial.REDSTONE_TORCH);
        isRedstoneBlock.add(XMaterial.REDSTONE_WALL_TORCH);
        isRedstoneBlock.add(XMaterial.REDSTONE_WIRE);
        isRedstoneBlock.addAll(ItemUtils.getEndWith("DOOR"));
        isRedstoneBlock.add(XMaterial.TNT);
        isRedstoneBlock.add(XMaterial.DISPENSER);
        isRedstoneBlock.add(XMaterial.NOTE_BLOCK);
        isRedstoneBlock.add(XMaterial.REPEATER);
        isRedstoneBlock.add(XMaterial.TRIPWIRE_HOOK);
        isRedstoneBlock.add(XMaterial.COMMAND_BLOCK);
        isRedstoneBlock.addAll(ItemUtils.getEndWith("_BUTTON"));
        isRedstoneBlock.add(XMaterial.TRAPPED_CHEST);
        isRedstoneBlock.add(XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE);
        isRedstoneBlock.add(XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE);
        isRedstoneBlock.add(XMaterial.COMPARATOR);
        isRedstoneBlock.add(XMaterial.REDSTONE_BLOCK);
        isRedstoneBlock.add(XMaterial.HOPPER);
        isRedstoneBlock.add(XMaterial.ACTIVATOR_RAIL);
        isRedstoneBlock.add(XMaterial.DROPPER);
        isRedstoneBlock.add(XMaterial.DAYLIGHT_DETECTOR);
    }


    /**
     * Returns true if a block uses Redstone in some way.
     *
     * @param id the type ID of the block
     * @return true if the block uses Redstone
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isRedstoneBlock(XMaterial id) {
        return isRedstoneBlock.contains(id);
    }

    @SuppressWarnings("DuplicatedCode")
    public void handleRedstoneForBlock(Block block, int oldLevel, int newLevel) {

        World world = block.getWorld();

        // Give the method a BlockWorldVector instead of a Block
        boolean wasOn = oldLevel >= 1;
        boolean isOn = newLevel >= 1;
        boolean wasChange = wasOn != isOn;

        // For efficiency reasons, we're only going to consider changes between
        // off and on state, and ignore simple current changes (i.e. 15->13)
        if (!wasChange) return;

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        // When this hook has been called, the level in the world has not
        // yet been updated, so we're going to do this very ugly thing of
        // faking the value with the new one whenever the data value of this
        // block is requested -- it is quite ugly
        switch (XMaterial.matchXMaterial(block.getType())) {
            case REDSTONE_WIRE:
                XMaterial above = XMaterial.matchXMaterial(world.getBlockAt(x, y + 1, z).getType());

                XMaterial westSide = XMaterial.matchXMaterial(world.getBlockAt(x, y, z + 1).getType());
                XMaterial westSideAbove = XMaterial.matchXMaterial(world.getBlockAt(x, y + 1, z + 1).getType());
                XMaterial westSideBelow = XMaterial.matchXMaterial(world.getBlockAt(x, y - 1, z + 1).getType());
                XMaterial eastSide = XMaterial.matchXMaterial(world.getBlockAt(x, y, z - 1).getType());
                XMaterial eastSideAbove = XMaterial.matchXMaterial(world.getBlockAt(x, y + 1, z - 1).getType());
                XMaterial eastSideBelow = XMaterial.matchXMaterial(world.getBlockAt(x, y - 1, z - 1).getType());

                XMaterial northSide = XMaterial.matchXMaterial(world.getBlockAt(x - 1, y, z).getType());
                XMaterial northSideAbove = XMaterial.matchXMaterial(world.getBlockAt(x - 1, y + 1, z).getType());
                XMaterial northSideBelow = XMaterial.matchXMaterial(world.getBlockAt(x - 1, y - 1, z).getType());
                XMaterial southSide = XMaterial.matchXMaterial(world.getBlockAt(x + 1, y, z).getType());
                XMaterial southSideAbove = XMaterial.matchXMaterial(world.getBlockAt(x + 1, y + 1, z).getType());
                XMaterial southSideBelow = XMaterial.matchXMaterial(world.getBlockAt(x + 1, y - 1, z).getType());

                // Make sure that the wire points to only this block
                if (!isRedstoneBlock(westSide) && !isRedstoneBlock(eastSide)
                        && (!isRedstoneBlock(westSideAbove) || westSide == XMaterial.AIR || above != XMaterial.AIR)
                        && (!isRedstoneBlock(eastSideAbove) || eastSide == XMaterial.AIR || above != XMaterial.AIR)
                        && (!isRedstoneBlock(westSideBelow) || westSide != XMaterial.AIR)
                        && (!isRedstoneBlock(eastSideBelow) || eastSide != XMaterial.AIR)) {
                    // Possible blocks north / south
                    handleDirectWireInput(x - 1, y, z, block, oldLevel, newLevel);
                    handleDirectWireInput(x + 1, y, z, block, oldLevel, newLevel);
                    handleDirectWireInput(x - 1, y - 1, z, block, oldLevel, newLevel);
                    handleDirectWireInput(x + 1, y - 1, z, block, oldLevel, newLevel);
                }

                if (!isRedstoneBlock(northSide) && !isRedstoneBlock(southSide)
                        && (!isRedstoneBlock(northSideAbove) || northSide == XMaterial.AIR || above != XMaterial.AIR)
                        && (!isRedstoneBlock(southSideAbove) || southSide == XMaterial.AIR || above != XMaterial.AIR)
                        && (!isRedstoneBlock(northSideBelow) || northSide != XMaterial.AIR)
                        && (!isRedstoneBlock(southSideBelow) || southSide != XMaterial.AIR)) {
                    // Possible blocks west / east
                    handleDirectWireInput(x, y, z - 1, block, oldLevel, newLevel);
                    handleDirectWireInput(x, y, z + 1, block, oldLevel, newLevel);
                    handleDirectWireInput(x, y - 1, z - 1, block, oldLevel, newLevel);
                    handleDirectWireInput(x, y - 1, z + 1, block, oldLevel, newLevel);
                }

                // Can be triggered from below
                handleDirectWireInput(x, y + 1, z, block, oldLevel, newLevel);

                // Can be triggered from above
                handleDirectWireInput(x, y - 1, z, block, oldLevel, newLevel);
                return;
            case REPEATER:
            case COMPARATOR:
                BlockFace f = VersionUtilsFactory.getInstance().getRotation(block);
                handleDirectWireInput(x + f.getModX(), y, z + f.getModZ(), block, oldLevel, newLevel);
                if (XMaterial.matchXMaterial(block.getRelative(f).getType()) != XMaterial.AIR) {
                    handleDirectWireInput(x + f.getModX(), y - 1, z + f.getModZ(), block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX(), y + 1, z + f.getModZ(), block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX() + 1, y - 1, z + f.getModZ(), block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX() - 1, y - 1, z + f.getModZ(), block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX() + 1, y - 1, z + f.getModZ() + 1, block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX() - 1, y - 1, z + f.getModZ() - 1, block, oldLevel, newLevel);
                }
                return;
            case ACACIA_BUTTON:
            case BIRCH_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
            case LEVER:
                BlockFace face = VersionUtilsFactory.getInstance().getRotation(block);
                if (face != null) {
                    face = face.getOppositeFace();
                    handleDirectWireInput(x + face.getModX() * 2, y + face.getModY() * 2, z + face.getModZ() * 2, block, oldLevel, newLevel);
                }
                break;
            case POWERED_RAIL:
            case ACTIVATOR_RAIL:
                return;
        }

        // For redstone wires and repeaters, the code already exited this method
        // Non-wire blocks proceed

        handleDirectWireInput(x - 1, y, z, block, oldLevel, newLevel);
        handleDirectWireInput(x + 1, y, z, block, oldLevel, newLevel);
        handleDirectWireInput(x - 1, y - 1, z, block, oldLevel, newLevel);
        handleDirectWireInput(x + 1, y - 1, z, block, oldLevel, newLevel);
        handleDirectWireInput(x, y, z - 1, block, oldLevel, newLevel);
        handleDirectWireInput(x, y, z + 1, block, oldLevel, newLevel);
        handleDirectWireInput(x, y - 1, z - 1, block, oldLevel, newLevel);
        handleDirectWireInput(x, y - 1, z + 1, block, oldLevel, newLevel);

        // Can be triggered from below
        handleDirectWireInput(x, y + 1, z, block, oldLevel, newLevel);

        // Can be triggered from above
        handleDirectWireInput(x, y - 1, z, block, oldLevel, newLevel);
    }

    private void handleDirectWireInput(int x, int y, int z, Block sourceBlock, int oldLevel, int newLevel) {

        Block block = sourceBlock.getWorld().getBlockAt(x, y, z);
        if (sameBlock(sourceBlock.getLocation(), block.getLocation())) //The same block, don't run.
            return;
        final SourcedBlockRedstoneEvent event = new SourcedBlockRedstoneEvent(sourceBlock, block, oldLevel, newLevel);

        Bukkit.getPluginManager().callEvent(event);

    }

    public final double EQUALS_PRECISION = 0.0001;

    public boolean sameBlock(org.bukkit.Location a, org.bukkit.Location b) {

        return Math.abs(a.getX() - b.getX()) <= EQUALS_PRECISION && Math.abs(a.getY() - b.getY()) <= EQUALS_PRECISION
                && Math.abs(a.getZ() - b.getZ()) <= EQUALS_PRECISION;
    }

    public int getPin(Block sign, Block source) {
        BlockFace signFace = VersionUtilsFactory.getInstance().getRotation(sign);
        // Если каким то образом табличка стоит неправильно или сигнал идёт сверху
        if (!FaceUtils.isValidFace(signFace))
            return 0;
        BlockFace sourceFace = FaceUtils.getRelativeFace(sign.getLocation(), source.getLocation());
        if (sourceFace.equals(BlockFace.SELF))
            return 0;
        return FaceUtils.getPin(signFace, sourceFace);
    }

}
