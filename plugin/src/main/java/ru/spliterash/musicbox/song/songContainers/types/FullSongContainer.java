package ru.spliterash.musicbox.song.songContainers.types;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public interface FullSongContainer extends SubSongContainer {
    default ItemStack getItemStack() {
        return getItemStack(Collections.emptyList());
    }

    ItemStack getItemStack(List<String> extraLines);

    String getName();
}
