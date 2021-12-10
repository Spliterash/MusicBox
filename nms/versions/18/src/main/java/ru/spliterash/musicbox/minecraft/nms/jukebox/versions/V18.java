package ru.spliterash.musicbox.minecraft.nms.jukebox.versions;

import org.bukkit.block.Jukebox;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.minecraft.nms.OldNmsUtils;
import ru.spliterash.musicbox.minecraft.nms.jukebox.IJukebox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class V18 implements IJukebox {

    private final Object tile_entity;

    public V18(Jukebox jukebox) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        Field f = OldNmsUtils.getNMSClass("org.bukkit.craftbukkit", "block.CraftBlockEntityState").getDeclaredField("tileEntity");
        f.setAccessible(true);
        this.tile_entity = f.get(jukebox);
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
                .getMethod("asBukkitCopy", Class.forName("net.minecraft.world.item.ItemStack"));
        return (ItemStack) toBukkitItem.invoke(null, vanilaItem);
    }

    public void setJukebox(ItemStack item) {
        try {
            Object vanilaItem = this.toCraft(item);
            Method m = this.tile_entity.getClass().getMethod("a", vanilaItem.getClass());
            m.invoke(this.tile_entity, vanilaItem);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public ItemStack getJukebox() {
        try {
            Method m = this.tile_entity.getClass().getMethod("c");
            Object vanilaItem = m.invoke(this.tile_entity);
            return this.fromCraft(vanilaItem);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }
}
