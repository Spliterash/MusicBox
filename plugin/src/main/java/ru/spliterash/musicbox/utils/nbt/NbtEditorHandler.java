package ru.spliterash.musicbox.utils.nbt;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.inventory.ItemStack;

public class NbtEditorHandler implements NBTHandler {
    @Override
    public ItemStack setNbt(ItemStack item, String key, Integer value) {
        return NBTEditor.set(item, value, key);
    }

    @Override
    public int getNbt(ItemStack item, String key) {
        return NBTEditor.getInt(item, key);
    }
}
