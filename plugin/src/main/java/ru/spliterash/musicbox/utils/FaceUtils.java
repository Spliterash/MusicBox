package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class FaceUtils {
    private final Set<BlockFace> validSignFace = new HashSet<>();
    private final Set<BlockFace> searchFace = new HashSet<>();

    static {
        FaceUtils.validSignFace.add(BlockFace.EAST);
        FaceUtils.validSignFace.add(BlockFace.NORTH);
        FaceUtils.validSignFace.add(BlockFace.SOUTH);
        FaceUtils.validSignFace.add(BlockFace.WEST);

        searchFace.addAll(validSignFace);
        searchFace.add(BlockFace.UP);
        searchFace.add(BlockFace.DOWN);
    }

    /**
     * @param face Start from direction
     * @return clockwise direction
     */
    public BlockFace getClockWise(BlockFace face) {

        switch (face) {
            case NORTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.WEST;
            case WEST:
                return BlockFace.NORTH;

            default:
                return BlockFace.SELF;
        }
    }

    /**
     * @param face Start from direction
     * @return clockwise direction
     */
    public BlockFace getCounterClockWise(BlockFace face) {

        switch (face) {
            case NORTH:
                return BlockFace.WEST;
            case EAST:
                return BlockFace.NORTH;
            case SOUTH:
                return BlockFace.EAST;
            case WEST:
                return BlockFace.SOUTH;

            default:
                return BlockFace.SELF;
        }
    }

    /**
     * 0 - Прямо
     * 1 - Слева
     * 2 - Справа
     * 3 - Сзади
     */
    public int getPin(BlockFace from, BlockFace to) {
        // Прямое подключение
        if (from == to)
            return 0;
        if (getCounterClockWise(to) == from)
            return 1;
        if (getClockWise(to) == from)
            return 2;
        if (invertFace(to) == from)
            return 3;
        // Любые другие ситуации
        return 0;
    }

    public boolean isValidFace(BlockFace face) {
        return validSignFace.contains(face);
    }

    public BlockFace getRelativeFace(Location from, Location to) {
        Vector fromV = new Vector(from.getBlockX(), from.getBlockY(), from.getBlockZ());
        Vector toV = new Vector(to.getBlockX(), to.getBlockY(), to.getBlockZ());
        if (fromV.getX() > toV.getX() && from.getZ() == to.getZ())
            return BlockFace.WEST;
        else if (fromV.getX() < toV.getX() && from.getZ() == to.getZ())
            return BlockFace.EAST;
        else if (fromV.getX() == toV.getX() && from.getZ() > to.getZ())
            return BlockFace.NORTH;
        else if (fromV.getX() == toV.getX() && from.getZ() < to.getZ())
            return BlockFace.SOUTH;

        else
            return BlockFace.SELF;
    }

    public BlockFace invertFace(BlockFace face) {
        switch (face) {
            case EAST:
                return BlockFace.WEST;
            case NORTH:
                return BlockFace.SOUTH;
            case WEST:
                return BlockFace.EAST;
            case SOUTH:
                return BlockFace.NORTH;
            default:
                return BlockFace.SELF;
        }
    }

    /**
     * Возращает правильный поворот для таблички
     */
    public BlockFace normalizeFace(BlockFace face) {
        if (face.getModX() == 2)
            return BlockFace.EAST;
        if (face.getModX() == -2)
            return BlockFace.WEST;
        if (face.getModZ() == 2)
            return BlockFace.SOUTH;
        if (face.getModZ() == -2)
            return BlockFace.NORTH;
        for (BlockFace value : BlockFace.values()) {
            if (value.getModX() == face.getModX())
                return value;
        }
        return BlockFace.NORTH;
    }

    public <T extends BlockState> T getRelativeAround(Block block, Class<T> tClass) {
        for (BlockFace face : searchFace) {
            Block anotherBlock = block.getRelative(face);
            BlockState state = anotherBlock.getState();
            //noinspection ConstantConditions
            if (state == null)
                continue;
            if (tClass.isInstance(state))
                return tClass.cast(state);
        }
        return null;
    }
}
