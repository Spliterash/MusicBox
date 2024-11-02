package ru.spliterash.musicbox.minecraft.nms.jukebox.versions;

import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.bukkit.block.Jukebox;
import org.bukkit.craftbukkit.block.CraftJukebox;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.minecraft.nms.jukebox.IJukebox;

public class V21_2 implements IJukebox {
    private final JukeboxBlockEntity tileEntity;

    public V21_2(Jukebox jukebox) {
        CraftJukebox craft = (CraftJukebox) jukebox;
        tileEntity = craft.getTileEntity();
    }

    public void setJukebox(ItemStack item) {
        net.minecraft.world.item.ItemStack converted = CraftItemStack.asNMSCopy(item);
        tileEntity.setSongItemWithoutPlaying(converted, 0);

    }

    public ItemStack getJukebox() {
        net.minecraft.world.item.ItemStack nmsItem = tileEntity.getItem(0);
        if (nmsItem.isEmpty())
            return null;

        return CraftItemStack.asBukkitCopy(nmsItem);
    }
}
