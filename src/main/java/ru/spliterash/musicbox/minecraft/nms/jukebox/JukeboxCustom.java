package ru.spliterash.musicbox.minecraft.nms.jukebox;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

import java.lang.reflect.Method;

public interface JukeboxCustom {

    default Object toCraft(ItemStack item) throws Exception {
        Method toVanilaItem = NMSUtils.getNMSClass(
                "org.bukkit.craftbukkit",
                "inventory.CraftItemStack")
                .getMethod("asNMSCopy", ItemStack.class);
        return toVanilaItem.invoke(null, item);
    }

    default boolean isEmpty() {
        ItemStack item = getJukebox();
        return item == null || item.getType().equals(Material.AIR);
    }

    default ItemStack fromCraft(Object vanilaItem) throws Exception {
        Method toBukkitItem = NMSUtils.getNMSClass(
                "org.bukkit.craftbukkit",
                "inventory.CraftItemStack")
                .getMethod("asBukkitCopy", NMSUtils.getNMSClass("net.minecraft.server", "ItemStack"));
        return (ItemStack) toBukkitItem.invoke(null, vanilaItem);
    }

    void setJukebox(ItemStack item);

    ItemStack getJukebox();
}
