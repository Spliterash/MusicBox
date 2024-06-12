package ru.spliterash.musicbox.utils.nbt;

import org.bukkit.inventory.ItemStack;

public interface NBTHandler {
    ItemStack setNbt(ItemStack item, String key, Integer value);

    int getNbt(ItemStack item, String key);
}
