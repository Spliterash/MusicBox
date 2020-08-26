package ru.spliterash.musicbox.minecraft.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface InventoryAction {
    void onEvent(InventoryClickEvent e);
}
