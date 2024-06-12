package ru.spliterash.musicbox.utils.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.MusicBox;

public class BukkitNbtHandler implements NBTHandler {
    @Override
    public ItemStack setNbt(ItemStack item, String key, Integer value) {
        ItemStack newItem = item.clone();

        ItemMeta meta = newItem.getItemMeta();
        meta.getCustomTagContainer().setCustomTag(new NamespacedKey(MusicBox.getInstance(), key), ItemTagType.INTEGER, value);
        newItem.setItemMeta(meta);

        return newItem;
    }

    @Override
    public @Nullable int getNbt(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        Integer value = meta.getCustomTagContainer().getCustomTag(new NamespacedKey(MusicBox.getInstance(), key), ItemTagType.INTEGER);
        if (value == null)
            value = 0;

        return value;
    }
}
