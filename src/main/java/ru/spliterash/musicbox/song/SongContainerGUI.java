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

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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
     * Открывает инвентарь игркоу
     *
     * @param page                  Страница инвентаря
     * @param showControls          Показывать кнопки остановки, паузы, настройки звука и тд
     * @param musicLore             Какой лор добавлять к иконке звука
     * @param onSongLeftClick       Действие при клике на левую кнопку мыши
     * @param onSongRightClick      Действие при клике на правую кнопку мыши
     * @param containerLore         Какой лор добавлять в контейнерам
     * @param onContainerRightClick Действие при левом клике на сундук
     */
    public void openPage(
            int page,
            boolean showControls,
            @Nullable Function<MusicBoxSong, List<String>> musicLore,
            @Nullable BiConsumer<Player, MusicBoxSong> onSongLeftClick,
            @Nullable BiConsumer<Player, MusicBoxSong> onSongRightClick,
            @Nullable Function<MusicBoxSongContainer, List<String>> containerLore,
            @Nullable BiConsumer<Player, MusicBoxSongContainer> onContainerRightClick
    ) {
        gui.changeTitle(Lang.GUI_TITLE.toString(
                "{container}", container.getName(),
                "{page}", String.valueOf(page + 1),
                "{last_page}", String.valueOf(getPageCount())
        ));
        open();
        //Проще в начале цикла сразу плюсовать, так что минус один чтобы началось с 0
        int inventoryIndex = -1;
        int indexLimit = 45;
        //Сколько элементов надо пропустить, чувствую тут надо +1 написать, но потом затестим
        int skipElements = page * indexLimit;
        inventoryFill:
        {
            List<MusicBoxSongContainer> subContainers = container.getSubContainers();
            for (int i = skipElements; i < subContainers.size(); i++, skipElements++) {
                MusicBoxSongContainer subContainer = subContainers.get(i);
                if (inventoryIndex++ >= indexLimit)
                    break inventoryFill;
                List<String> extraLines;
                if (containerLore != null)
                    extraLines = containerLore.apply(subContainer);
                else
                    extraLines = Collections.emptyList();
                ItemStack containerStack = subContainer.getItemStack(extraLines);
                Consumer<Player> containerConsumer;
                if (onContainerRightClick != null)
                    containerConsumer = player -> onContainerRightClick.accept(player, subContainer);
                else
                    containerConsumer = null;
                GUI.InventoryAction containerAction = new GUI.InventoryAction(
                        p -> new SongContainerGUI(subContainer, player)
                                .openPage(
                                        page,
                                        showControls,
                                        musicLore,
                                        onSongLeftClick,
                                        onSongRightClick,
                                        containerLore,
                                        onContainerRightClick),
                        containerConsumer,
                        null);
                gui.addItem(inventoryIndex, containerStack, containerAction);
            }
            PeekList<XMaterial> list = new PeekList<>(BukkitUtils.DISCS);
            List<MusicBoxSong> songs = container.getSongs();
            for (int i = skipElements; i < songs.size(); i++) {
                MusicBoxSong song = songs.get(i);
                if (inventoryIndex++ >= indexLimit)
                    break inventoryFill;
                List<String> extraLines;
                if (musicLore != null)
                    extraLines = musicLore.apply(song);
                else
                    extraLines = Collections.emptyList();
                ItemStack stack = song.getSongStack(list.peek(), extraLines);
                gui.addItem(inventoryIndex, stack,
                        new GUI.InventoryAction(
                                p -> {
                                    if (onSongLeftClick != null)
                                        onSongLeftClick.accept(p, song);
                                },
                                p -> {
                                    if (onSongRightClick != null)
                                        onSongRightClick.accept(p, song);
                                },
                                null
                        ));
            }
        }
        //TODO Добавить кнопки управления
    }

    private int getPageCount() {
        int containerElementSize = container.getSubContainers().size() + container.getAllSongs().size();
        return (int) Math.floor(containerElementSize / 45D);
    }
}
