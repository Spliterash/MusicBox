package ru.spliterash.musicbox.minecraft.gui.actions;

import org.bukkit.event.inventory.InventoryClickEvent;
import ru.spliterash.musicbox.minecraft.gui.InventoryAction;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ClickAction implements InventoryAction {
    private final Optional<Runnable> left;
    private final Optional<Runnable> right;

    public ClickAction(Runnable left, Runnable right) {
        this.left = Optional.ofNullable(left);
        this.right = Optional.ofNullable(right);
    }

    public ClickAction(Runnable left) {
        this(left, null);
    }


    @Override
    final public void onEvent(InventoryClickEvent e) {
        switch (e.getClick()) {
            case LEFT:
                left.ifPresent(Runnable::run);
                break;
            case RIGHT:
                right.ifPresent(Runnable::run);
                break;
        }
    }
}
