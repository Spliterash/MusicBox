package ru.spliterash.musicbox.gui.song;

import com.cryptomorin.xseries.XMaterial;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.minecraft.GUI;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongContainer;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.ItemUtils;
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
     * @param page   Страница инвентар
     * @param params Параметры инвентаря
     */
    public void openPage(int page, SongGUIParams params) {
        int inventoryIndex = -1;
        int indexLimit = 45;
        //Сколько элементов надо пропустить, чувствую тут надо +1 написать, но потом затестим
        int skipElements = page * indexLimit;
        int pageCount = getPageCount();

        GUI gui = createGUI(Lang.GUI_TITLE.toString(
                "{container}", container.getName(),
                "{page}", String.valueOf(page + 1),
                "{last_page}", String.valueOf(pageCount)
        ));
        gui.open(getWrapper().getPlayer());
        inventoryFill:
        {
            List<MusicBoxSongContainer> subContainers = container.getSubContainers();
            for (int i = skipElements; i < subContainers.size(); i++, skipElements++) {
                MusicBoxSongContainer subContainer = subContainers.get(i);
                SongGUIData<MusicBoxSongContainer> data = new SongGUIData<>(
                        this,
                        subContainer,
                        params,
                        page
                );
                if (inventoryIndex++ >= indexLimit)
                    break inventoryFill;
                List<String> extraLines;
                if (params.getExtraContainerLore() != null)
                    extraLines = params.getExtraContainerLore().apply(data);
                else
                    extraLines = Collections.emptyList();
                ItemStack containerStack = subContainer.getItemStack(extraLines);
                Consumer<Player> containerConsumer;
                if (params.getOnContainerRightClick() != null)
                    containerConsumer = player -> params.getOnContainerRightClick().accept(wrapper, data);
                else
                    containerConsumer = null;
                GUI.InventoryAction containerAction = new GUI.InventoryAction(
                        p -> subContainer.createGUI(wrapper)
                                .openPage(0, params),
                        containerConsumer,
                        null);
                gui.addItem(inventoryIndex, containerStack, containerAction);
            }
            PeekList<XMaterial> list = new PeekList<>(BukkitUtils.DISCS);
            List<MusicBoxSong> songs = container.getSongs();
            MusicBoxSong playerSong = wrapper.getActivePlayer() != null ? wrapper.getActivePlayer().getMusicBoxSong() : null;
            for (int i = skipElements; i < songs.size(); i++) {
                MusicBoxSong song = songs.get(i);
                SongGUIData<MusicBoxSong> data = new SongGUIData<>(this, song, params, page);
                if (inventoryIndex++ >= indexLimit)
                    break inventoryFill;
                List<String> extraLines;
                if (params.getExtraSongLore() != null)
                    extraLines = params.getExtraSongLore().apply(data);
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
                                    if (params.getOnSongLeftClick() != null)
                                        params.getOnSongLeftClick().accept(wrapper, data);
                                },
                                p -> {
                                    if (params.getOnSongRightClick() != null)
                                        params.getOnSongRightClick().accept(wrapper, data);
                                },
                                null
                        ));
            }
        }

        @Nullable BarButton[] bottomBar;
        if (params.getBottomBar() != null) {
            bottomBar = params.getBottomBar();
            if (bottomBar.length > 7)
                throw new RuntimeException("Length bigger 6");
            int startIndex = 47;
            for (int i = 0; i < bottomBar.length; i++) {
                BarButton button = bottomBar[i];
                if (button == null)
                    continue;
                ItemStack stack = button.getItemStack(wrapper);
                if (stack == null)
                    continue;
                GUI.InventoryAction action = new GUI.InventoryAction(p ->
                        button.processClick(
                                wrapper,
                                new SongGUIData<>(this, null, params, page))
                );
                gui.addItem(i + startIndex, stack, action);
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
                            .openPage(0, params))
            );
        }
        // Пагинация
        if (page > 0)
            gui.addItem(
                    45,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.BACK.toString(), null),
                    new GUI.InventoryAction(p -> openPage(page - 1, params)));
        if (getPageCount() > page && page > 0)
            gui.addItem(
                    53,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.NEXT.toString(), null),
                    new GUI.InventoryAction(p -> openPage(
                            page + 1, params)));

    }

    private GUI createGUI(String title) {
        return new GUI(title);
    }

    private int getPageCount() {
        int containerElementSize = container.getSubContainers().size() + container.getSongs().size();
        return (int) Math.ceil(containerElementSize / 45D);
    }

    @Getter
    @AllArgsConstructor
    public abstract static class BarButton {
        public abstract ItemStack getItemStack(PlayerWrapper wrapper);

        public abstract void processClick(PlayerWrapper wrapper, SongGUIData<Void> data);
    }


    @Getter
    @Builder
    public static class SongGUIParams {
        /**
         * Возможно выглядит слишком длинно
         * Ключ это получение предмета
         * Значение это действие при клике
         */
        @Nullable
        private final BarButton[] bottomBar;
        @Nullable
        private final Function<SongGUIData<MusicBoxSong>, List<String>> extraSongLore;
        @Nullable
        private final BiConsumer<PlayerWrapper, SongGUIData<MusicBoxSong>> onSongLeftClick;
        @Nullable
        private final BiConsumer<PlayerWrapper, SongGUIData<MusicBoxSong>> onSongRightClick;
        @Nullable
        private final Function<SongGUIData<MusicBoxSongContainer>, List<String>> extraContainerLore;
        @Nullable
        private final BiConsumer<PlayerWrapper, SongGUIData<MusicBoxSongContainer>> onContainerRightClick;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public class SongGUIData<T> {
        private final SongContainerGUI gui;
        private final T data;
        private final SongGUIParams params;
        private final int page;

        /**
         * Обновляет подсвеченный айтем
         * Возможно кривовато, но ничего лучше я не придумал
         */
        public void refreshInventory() {
            openPage(page, params);
        }
    }
}
