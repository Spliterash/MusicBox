package ru.spliterash.musicbox.minecraft.nms.jukebox.versions;

import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.minecraft.nms.jukebox.IJukebox;
import ru.spliterash.musicbox.minecraft.nms.OldNmsUtils;

import java.lang.reflect.Method;

public class V12 implements IJukebox {

    private final Object tile_entity;

    public V12(Jukebox box) throws Exception {
        Method m = OldNmsUtils.getNMSClass("org.bukkit.craftbukkit", "block.CraftBlockEntityState").getDeclaredMethod("getTileEntity");
        m.setAccessible(true);
        this.tile_entity = m.invoke(box);
    }

    public Object toCraft(ItemStack item) throws Exception {
        Method toVanilaItem = OldNmsUtils.getNMSClass(
                "org.bukkit.craftbukkit",
                "inventory.CraftItemStack")
                .getMethod("asNMSCopy", ItemStack.class);
        return toVanilaItem.invoke(null, item);
    }

    public ItemStack fromCraft(Object vanilaItem) throws Exception {
        Method toBukkitItem = OldNmsUtils.getNMSClass(
                "org.bukkit.craftbukkit",
                "inventory.CraftItemStack")
                .getMethod("asBukkitCopy", OldNmsUtils.getNMSClass("net.minecraft.server", "ItemStack"));
        return (ItemStack) toBukkitItem.invoke(null, vanilaItem);
    }

    @Override
    public void setJukebox(ItemStack item) {
        try {
            Object vanilaItem = this.toCraft(item);
            Method m = this.tile_entity.getClass().getMethod("setRecord", vanilaItem.getClass());
            m.invoke(this.tile_entity, vanilaItem);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ItemStack getJukebox() {
        try {
            Method m = tile_entity.getClass().getMethod("getRecord");
            Object vanilaItem = m.invoke(this.tile_entity);
            return fromCraft(vanilaItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
