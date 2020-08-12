package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@UtilityClass
public class ItemUtils {
    public ItemStack glow(ItemStack stack) {
        NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound("[{}]");
        return NBTEditor.set(stack, compound, "Enchantments");
    }

    public ItemStack createStack(XMaterial material, String name, @Nullable List<String> lore) {
        ItemStack stack = material.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null)
            meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
