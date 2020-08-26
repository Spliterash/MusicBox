package ru.spliterash.musicbox.minecraft.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ClickAction implements InventoryAction {
    private final Optional<Consumer<Player>> left;
    private final Optional<Consumer<Player>> right;

    public ClickAction(Consumer<Player> onLeftClick, Consumer<Player> onRightClick) {
        this.left = Optional.ofNullable(onLeftClick);
        this.right = Optional.ofNullable(onRightClick);
    }

    public ClickAction(Consumer<Player> onLeftClick) {
        this(onLeftClick, null);
    }

    @Override
    final public void onEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        switch (e.getClick()) {
            case LEFT:
                left.ifPresent(c -> c.accept(p));
                break;
            case RIGHT:
                right.ifPresent(r -> r.accept(p));
        }
    }
}
