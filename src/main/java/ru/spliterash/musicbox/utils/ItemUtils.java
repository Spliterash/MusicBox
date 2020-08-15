package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.experimental.UtilityClass;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@UtilityClass
public class ItemUtils {
    private final Enchantment enchantment = XEnchantment.PROTECTION_EXPLOSIONS.parseEnchantment();

    public ItemStack glow(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(enchantment, 9999, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
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

    public boolean isGlow(ItemStack item) {
        return item.getEnchantments().containsKey(enchantment);
    }

    public ItemStack unGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.removeEnchant(enchantment);
        item.setItemMeta(meta);
        return item;
    }
}
