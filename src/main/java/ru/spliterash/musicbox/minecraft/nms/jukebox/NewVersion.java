//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.spliterash.musicbox.minecraft.nms.jukebox;

import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NewVersion implements JukeboxCustom {
    private final Object tile_entity;

    NewVersion(Jukebox box) throws Exception {
        Field f = NMSUtils.getNMSClass("org.bukkit.craftbukkit", "block.CraftBlockEntityState").getDeclaredField("tileEntity");
        f.setAccessible(true);
        this.tile_entity = f.get(box);
    }

    public void setJukebox(ItemStack item) {
        try {
            Object vanilaItem = this.toCraft(item);
            Method m = this.tile_entity.getClass().getMethod("setRecord", vanilaItem.getClass());
            m.invoke(this.tile_entity, vanilaItem);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public ItemStack getJukebox() {
        try {
            Method m = this.tile_entity.getClass().getMethod("getRecord");
            Object vanilaItem = m.invoke(this.tile_entity);
            return this.fromCraft(vanilaItem);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }
}
