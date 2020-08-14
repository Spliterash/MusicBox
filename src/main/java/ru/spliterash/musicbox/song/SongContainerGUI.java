package ru.spliterash.musicbox.song;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.minecraft.GUI;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.ItemUtils;
import ru.spliterash.musicbox.utils.classes.Pair;
import ru.spliterash.musicbox.utils.classes.PeekList;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class SongContainerGUI {
    private final MusicBoxSongContainer container;
    private final PlayerWrapper wrapper;

    public SongContainerGUI(MusicBoxSongContainer container, PlayerWrapper wrapper) {
        this.container = container;
        this.wrapper = wrapper;
    }

    /**
     * Открывает инвентарь игркоу
     *
     * @param page                  Страница инвентаря
     * @param bottomBar             Наполнение полосы снизу не считая кнопок управления(не больше 6 элементов)
     * @param extraMusicLore        Какой лор добавлять к иконке звука
     * @param onSongLeftClick       Действие при клике на левую кнопку мыши
     * @param onSongRightClick      Действие при клике на правую кнопку мыши
     * @param extraContainerLore    Какой лор добавлять в контейнерам
     * @param onContainerRightClick Действие при левом клике на сундук
     */
    public void openPage(
            int page,
            @Nullable Pair<ItemStack, GUI.InventoryAction>[] bottomBar,
            @Nullable Function<MusicBoxSong, List<String>> extraMusicLore,
            @Nullable BiConsumer<Player, MusicBoxSong> onSongLeftClick,
            @Nullable BiConsumer<Player, MusicBoxSong> onSongRightClick,
            @Nullable Function<MusicBoxSongContainer, List<String>> extraContainerLore,
            @Nullable BiConsumer<Player, MusicBoxSongContainer> onContainerRightClick
    ) {
        int pageCount = getPageCount();
        GUI gui = createGUI(Lang.GUI_TITLE.toString(
                "{container}", container.getName(),
                "{page}", String.valueOf(page + 1),
                "{last_page}", String.valueOf(pageCount)
        ));
        gui.open(wrapper.getPlayer());
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
                if (extraContainerLore != null)
                    extraLines = extraContainerLore.apply(subContainer);
                else
                    extraLines = Collections.emptyList();
                ItemStack containerStack = subContainer.getItemStack(extraLines);
                Consumer<Player> containerConsumer;
                if (onContainerRightClick != null)
                    containerConsumer = player -> onContainerRightClick.accept(player, subContainer);
                else
                    containerConsumer = null;
                GUI.InventoryAction containerAction = new GUI.InventoryAction(
                        p -> subContainer.createGUI(wrapper)
                                .openPage(
                                        0,
                                        bottomBar,
                                        extraMusicLore,
                                        onSongLeftClick,
                                        onSongRightClick,
                                        extraContainerLore,
                                        onContainerRightClick),
                        containerConsumer,
                        null);
                gui.addItem(inventoryIndex, containerStack, containerAction);
            }
            PeekList<XMaterial> list = new PeekList<>(BukkitUtils.DISCS);
            List<MusicBoxSong> songs = container.getSongs();
            MusicBoxSong playerSong = wrapper.getActivePlayer() != null ? wrapper.getActivePlayer().getMusicBoxSong() : null;
            for (int i = skipElements; i < songs.size(); i++) {
                MusicBoxSong song = songs.get(i);
                if (inventoryIndex++ >= indexLimit)
                    break inventoryFill;
                List<String> extraLines;
                if (extraMusicLore != null)
                    extraLines = extraMusicLore.apply(song);
                else
                    extraLines = Collections.emptyList();
                boolean enchanted;
                if (playerSong != null)
                    enchanted = song.equals(playerSong);
                else
                    enchanted = false;
                ItemStack stack = song.getSongStack(list.peek(), extraLines, enchanted);
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
        // Возможно криво что в методе столько параметров
        // но по другому хз как
        if (bottomBar != null) {
            if (bottomBar.length > 7)
                throw new RuntimeException("Length bigger 6");
            int startIndex = 47;
            for (int i = 0; i < bottomBar.length; i++) {
                @Nullable Pair<ItemStack, GUI.InventoryAction> pair = bottomBar[i];
                if (pair == null)
                    continue;
                gui.addItem(i + startIndex, pair.getKey(), pair.getValue());
            }
        }
        //Добавление выхода на уровень выше, если он есть
        parentContainer:
        {
            MusicBoxSongContainer parentContainer = container.getParent();
            if (parentContainer == null)
                break parentContainer;
            gui.addItem(
                    46,
                    ItemUtils.createStack(XMaterial.TORCH, Lang.PARENT_CONTAINER.toString(), null),
                    new GUI.InventoryAction(p -> parentContainer
                            .createGUI(wrapper)
                            .openPage(
                                    0,
                                    bottomBar,
                                    extraMusicLore,
                                    onSongLeftClick,
                                    onSongRightClick,
                                    extraContainerLore,
                                    onContainerRightClick))
            );
        }
        // Пагинация
        if (page > 0)
            gui.addItem(
                    45,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.NEXT.toString(), null),
                    new GUI.InventoryAction(p -> openPage(
                            page - 1, bottomBar,
                            extraMusicLore,
                            onSongLeftClick,
                            onSongRightClick,
                            extraContainerLore,
                            onContainerRightClick)));
        if (pageCount > page)
            gui.addItem(
                    53,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.NEXT.toString(), null),
                    new GUI.InventoryAction(p -> openPage(
                            page + 1, bottomBar,
                            extraMusicLore,
                            onSongLeftClick,
                            onSongRightClick,
                            extraContainerLore,
                            onContainerRightClick)));

    }

    private GUI createGUI(String title) {
        return new GUI(title);
    }

    private int getPageCount() {
        int containerElementSize = container.getSubContainers().size() + container.getAllSongs().size();
        return (int) Math.floor(containerElementSize / 45D);
    }
}
