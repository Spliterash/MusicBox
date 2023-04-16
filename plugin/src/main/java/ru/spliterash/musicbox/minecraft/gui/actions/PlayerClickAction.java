package ru.spliterash.musicbox.minecraft.gui.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.spliterash.musicbox.minecraft.gui.InventoryAction;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PlayerClickAction implements InventoryAction {
    private final Optional<Consumer<Player>> left;
    private final Optional<Consumer<Player>> right;

    public PlayerClickAction(Consumer<Player> onLeftClick, Consumer<Player> onRightClick) {
        this.left = Optional.ofNullable(onLeftClick);
        this.right = Optional.ofNullable(onRightClick);
    }

    public PlayerClickAction(Consumer<Player> onLeftClick) {
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
                break;
        }
    }
}
