package ru.spliterash.musicbox.minecraft.nms.jukebox;

import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.minecraft.nms.NMSUtils;

import java.lang.reflect.Method;

public class v1_12 implements JukeboxCustom {
    private final Object tile_entity;

    v1_12(Jukebox box) throws Exception {
        Method m = NMSUtils.getNMSClass("org.bukkit.craftbukkit", "block.CraftBlockEntityState").getDeclaredMethod("getTileEntity");
        m.setAccessible(true);
        this.tile_entity = m.invoke(box);
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
