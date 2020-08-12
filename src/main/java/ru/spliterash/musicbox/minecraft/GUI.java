package ru.spliterash.musicbox.minecraft;


import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.utils.BukkitUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class GUI implements InventoryHolder {

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (e.getInventory().getHolder() instanceof GUI) {
                    ((GUI) e.getInventory().getHolder()).onInventoryClick(e);
                }
            }

        }, MusicBox.getInstance());
    }

    @Getter
    private Inventory inventory;
    private final Map<Integer, InventoryAction> runnableMap = new HashMap<>();

    public GUI(String title, int rows) {
        createInventory(title, rows);
    }

    private void createInventory(String title, int rows) {
        if (Bukkit.isPrimaryThread())
            inventory = Bukkit.createInventory(this, 9 * rows, title);
        else {
            try {
                inventory = Bukkit
                        .getScheduler()
                        .callSyncMethod(
                                MusicBox.getInstance(),
                                () -> Bukkit.createInventory(GUI.this, 9 * rows, title))
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public GUI(String title) {
        this(title, 6);
    }

    public void open(Player player) {
        BukkitUtils.runSyncTask(() -> player.openInventory(getInventory()));
    }

    public void clear() {
        inventory.clear();
        runnableMap.clear();
    }

    public void addItem(int slot, ItemStack item, InventoryAction runnable) {
        inventory.setItem(slot, item);
        runnableMap.put(slot, runnable);
    }

    public void removeItem(int slot) {
        inventory.clear(slot);
        runnableMap.remove(slot);
    }

    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getClickedInventory() == null || e.getClickedInventory().getHolder() != this)
            return;
        InventoryAction action = runnableMap.get(e.getSlot());
        if (action == null)
            return;
        switch (e.getClick()) {
            case LEFT:
                action.click((Player) e.getWhoClicked());
                break;
            case RIGHT:
                action.rightClick((Player) e.getWhoClicked());
                break;
            case SHIFT_LEFT:
                action.shiftClick((Player) e.getWhoClicked());
                break;
        }
    }

    /**
     * Поскольку у созданого инвентаря нельзя сменить титл, то только так
     *
     * @param title Новый титл
     * @return Новый GUI с новым титлом
     */
    public GUI cloneWithNewTitle(String title) {
        ItemStack[] content = inventory.getContents();
        GUI gui = new GUI(title, content.length);
        gui.runnableMap.putAll(runnableMap);
        gui.inventory.setContents(content);
        return gui;
    }

    public static class InventoryAction {
        private final Consumer<Player> click, rightClick, shiftClick;

        public InventoryAction(Consumer<Player> click) {
            this(click, null, null);
        }

        public InventoryAction(Consumer<Player> click, Consumer<Player> rightClick, Consumer<Player> shiftClick) {
            this.click = click;
            this.rightClick = rightClick;
            this.shiftClick = shiftClick;
        }

        public void click(Player p) {
            click.accept(p);
        }

        public void rightClick(Player p) {
            if (rightClick != null) {
                rightClick.accept(p);
            }
        }

        public void shiftClick(Player p) {
            if (shiftClick != null) {
                shiftClick.accept(p);
            }
        }
    }
}