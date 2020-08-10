package ru.spliterash.musicbox.song;

import com.cryptomorin.xseries.XMaterial;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.minecraft.GUI;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.classes.PeekList;

import java.util.function.Consumer;

@Getter
public class SongContainerGUI {
    private final MusicBoxSongContainer container;
    private final Player player;
    @Getter(AccessLevel.NONE)
    private final GUI gui;

    public SongContainerGUI(MusicBoxSongContainer container, Player player) {
        this.container = container;
        this.player = player;
        this.gui = new GUI(container.getName());
    }

    public void open() {
        gui.open(player);
    }

    /**
     * Поменять содержимое инвентаря на страницу
     *
     * @param page номер страницы
     */
    public void openPage(int page, boolean showControls, GUI.InventoryAction onSongClick, @Nullable Consumer<Player> onContainerRight) {
        //А терь говнокод
        gui.changeTitle(Lang.GUI_TITLE.toString(
                "{container}", container.getName(),
                "{page}", String.valueOf(page + 1),
                "{last_page}", String.valueOf(getPageCount())
        ));
        //Проще в начале цикла сразу плюсовать, так что минус один чтобы началось с 0
        int inventoryIndex = -1;
        int indexLimit = 45;
        inventoryFill:
        {
            for (MusicBoxSongContainer subContainer : container.getSubContainers()) {
                if (inventoryIndex++ >= indexLimit)
                    break inventoryFill;
                ItemStack containerStack = subContainer.getItemStack();
                GUI.InventoryAction containerAction = new GUI.InventoryAction(
                        p -> new SongContainerGUI(subContainer, player)
                                .openPage(page, showControls, onSongClick, onContainerRight),
                        onContainerRight, null);
                gui.addItem(inventoryIndex, containerStack, containerAction);
            }
            PeekList<XMaterial> list = new PeekList<>(BukkitUtils.DISCS);
            for (MusicBoxSong song : container.getSongs()) {
                if (inventoryIndex++ >= indexLimit)
                    break inventoryFill;
                ItemStack stack = song.getSongStack(list.peek());
                gui.addItem(inventoryIndex, stack, onSongClick);
            }
        }
        //TODO Добавить кнопки управления

        gui.open(player);
    }

    private int getPageCount() {
        int containerElementSize = container.getSubContainers().size() + container.getAllSongs().size();
        return (int) Math.floor(containerElementSize / 45D);
    }
}
