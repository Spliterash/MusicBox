package ru.spliterash.musicbox.gui;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.minecraft.GUI;
import ru.spliterash.musicbox.utils.ItemUtils;
import ru.spliterash.musicbox.utils.StringUtils;

/**
 * Используется для перемотки любых проигрывателей с помощью GUI
 * Для каждого проигрывателя создаётся одна копия перемотки (если потребуется)
 * Так что нет необходимости создавать новый объект каждый раз
 */
public class RewindGUI {
    private final MusicBoxSongPlayer musicPlayer;
    private final GUI gui;

    public RewindGUI(MusicBoxSongPlayer player) {
        this.musicPlayer = player;
        this.gui = new GUI(Lang.REWIND_GUI_TITLE.toString("{song}", musicPlayer.getMusicBoxSong().getName()), 3);
    }

    public void refreshInventory() {
        gui.clear();
        short allTicks = musicPlayer.getMusicBoxSong().getLength();
        short currentTick = musicPlayer.getTick();
        float speed = musicPlayer.getMusicBoxSong().getSpeed();
        short chunkSize = (short) Math.ceil(allTicks / 9D);
        for (int i = 0; i < 9; i++) {
            int currentIndex = i + 9;
            short chunkStart = (short) (i * chunkSize);
            XMaterial material;
            if (currentTick >= chunkStart) {
                material = XMaterial.WHITE_STAINED_GLASS_PANE;
            } else {
                material = XMaterial.GRAY_STAINED_GLASS_PANE;
            }
            String[] rewindReplaceArray = new String[]{
                    "{percent}", String.valueOf((int) Math.floor(((double) chunkStart / (double) allTicks) * 100)),
                    "{time}", StringUtils.toHumanTime((int) Math.floor(chunkStart / speed))
            };
            gui.addItem(
                    currentIndex,
                    ItemUtils.createStack(
                            material,
                            Lang.REWIND_TO.toString(rewindReplaceArray),
                            null
                    ),
                    new GUI.InventoryAction(
                            p -> {
                                musicPlayer.getApiPlayer().setTick(chunkStart);
                                p.sendMessage(Lang.REWINDED.toString(rewindReplaceArray));
                                refreshInventory();
                            }
                    )
            );
        }
        // Заполнить всё остальное выходом
        {
            ItemStack close = ItemUtils.createStack(XMaterial.RED_STAINED_GLASS_PANE, Lang.CLOSE.toString(), null);
            GUI.InventoryAction action = new GUI.InventoryAction(HumanEntity::closeInventory);
            for (int i = 0; i < gui.getInventory().getSize(); i++) {
                ItemStack item = gui.getInventory().getItem(i);
                if (item != null)
                    continue;
                gui.addItem(i, close, action);
            }
        }
    }

    /**
     * Открывает инвентарь этому игроку
     */
    public void openForPlayer(Player player) {
        refreshInventory();
        gui.open(player);
    }

}
