package ru.spliterash.musicbox.gui.song;

import com.cryptomorin.xseries.XMaterial;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.minecraft.gui.GUI;
import ru.spliterash.musicbox.minecraft.gui.InventoryAction;
import ru.spliterash.musicbox.minecraft.gui.actions.ClickAction;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.songContainers.types.FullSongContainer;
import ru.spliterash.musicbox.song.songContainers.types.SongContainer;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.ItemUtils;
import ru.spliterash.musicbox.utils.classes.PeekList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class SongContainerGUI {
    private final FullSongContainer container;
    private final PlayerWrapper wrapper;
    private final List<SongGUIItem> items = new LinkedList<>();

    public SongContainerGUI(FullSongContainer container, PlayerWrapper wrapper) {
        this.container = container;
        this.wrapper = wrapper;
        refreshItems();
    }


    public void refreshItems() {
        items.clear();

        container.getSubContainers().stream().map(SongGUIChest::new).collect(Collectors.toCollection(() -> this.items));

        container.getSongs().stream().map(SongGUISong::new).collect(Collectors.toCollection(() -> this.items));
    }


    /**
     * Открывает инвентарь игроку
     *
     * @param page   Страница инвентаря
     * @param params Параметры инвентаря
     */
    public void openPage(int page, SongGUIParams params) {
        int indexLimit = 45;
        int skipElements = page * indexLimit;
        int pageCount = getPageCount();

        GUI gui = createGUI(Lang.GUI_TITLE.toString(
                "{container}", container.getName(),
                "{page}", String.valueOf(page + 1),
                "{last_page}", String.valueOf(pageCount)
        ));
        gui.open(getWrapper().getPlayer());

        List<SongGUIItem> items = getItems();
        FullSongContainer container = this.container;


        PeekList<XMaterial> list = new PeekList<>(BukkitUtils.DISCS);
        List<MusicBoxSong> songs = container.getSongs();
        MusicBoxSong playerSong = wrapper.getActivePlayer() != null ? wrapper.getActivePlayer().getMusicBoxSong() : null;
        /*
          i - Индекс майнкрафтовского инвентаря
          currentListItem - текущий элемент из items
         */
        for (int i = 0, currentListItem = skipElements; i < 45 && currentListItem < items.size(); i++, currentListItem++) {
            SongGUIItem item = items.get(currentListItem);
            if (item instanceof SongGUIChest) {
                FullSongContainer chest = ((SongGUIChest) item).getContainer();
                SongGUIData<FullSongContainer> data = new SongGUIData<>(
                        this,
                        chest,
                        params,
                        page
                );

                List<String> extraLines;
                if (params.getExtraContainerLore() != null)
                    extraLines = params.getExtraContainerLore().apply(data);
                else
                    extraLines = Collections.emptyList();
                ItemStack containerStack;
                containerStack = chest.getItemStack(extraLines);
                Runnable containerConsumer;
                if (params.getOnContainerRightClick() != null)
                    containerConsumer = () -> params.getOnContainerRightClick().accept(wrapper, data);
                else
                    containerConsumer = null;
                ClickAction containerAction = new ClickAction(() -> new SongContainerGUI(chest, wrapper).openPage(0, params), containerConsumer);
                gui.addItem(i, containerStack, containerAction);
            } else if (item instanceof SongGUISong) {
                MusicBoxSong song = ((SongGUISong) item).getSong();
                SongGUIData<MusicBoxSong> data = new SongGUIData<>(this, song, params, page);


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
                ItemStack stack = song.getSongStack(list.getAndNext(), extraLines, enchanted);
                gui.addItem(i, stack,
                        new ClickAction(
                                () -> {
                                    if (params.getOnSongLeftClick() != null)
                                        params.getOnSongLeftClick().accept(wrapper, data);
                                },
                                () -> {
                                    if (params.getOnSongRightClick() != null)
                                        params.getOnSongRightClick().accept(wrapper, data);
                                }
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
                InventoryAction action = button.getAction(wrapper, new SongGUIData<>(this, null, params, page));
                gui.addItem(i + startIndex, stack, action);
            }
        }
        //Добавление выхода на уровень выше, если он есть
        if (container.getParentContainer() != null) {
            SongContainer parent = container.getParentContainer();

            if (!(parent instanceof FullSongContainer)) {
                wrapper.getPlayer().sendMessage("Sry, but plugin has error, this container is not GUI container, so i can show it");
                return;
            }

            gui.addItem(
                    46,
                    ItemUtils.createStack(XMaterial.TORCH, Lang.PARENT_CONTAINER.toString(), null),
                    new ClickAction(() -> new SongContainerGUI((FullSongContainer) container.getParentContainer(), wrapper)
                            .openPage(0, params))
            );
        }
        // Пагинация
        if (page > 0)
            gui.addItem(
                    45,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.BACK.toString(), null),
                    new ClickAction(() -> openPage(page - 1, params)));
        if ((pageCount - 1) > page)
            gui.addItem(
                    53,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.NEXT.toString(), null),
                    new ClickAction(() -> openPage(
                            page + 1, params)));

    }

    private GUI createGUI(String title) {
        return new GUI(title);
    }

    private int getPageCount() {
        int containerElementSize = items.size();
        return (int) Math.ceil(containerElementSize / 45D);
    }


    public interface BarButton {
        ItemStack getItemStack(PlayerWrapper wrapper);

        InventoryAction getAction(PlayerWrapper wrapper, SongGUIData<Void> data);
    }


    @Getter
    @Builder
    public static class SongGUIParams {
        @Nullable
        private final BarButton[] bottomBar;
        @Nullable
        private final Function<SongGUIData<MusicBoxSong>, List<String>> extraSongLore;
        @Nullable
        private final BiConsumer<PlayerWrapper, SongGUIData<MusicBoxSong>> onSongLeftClick;
        @Nullable
        private final BiConsumer<PlayerWrapper, SongGUIData<MusicBoxSong>> onSongRightClick;
        @Nullable
        private final Function<SongGUIData<FullSongContainer>, List<String>> extraContainerLore;
        @Nullable
        private final BiConsumer<PlayerWrapper, SongGUIData<FullSongContainer>> onContainerRightClick;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public class SongGUIData<T> {
        private final SongContainerGUI gui;
        private final T data;
        private final SongGUIParams params;
        private final int page;

        /**
         * Обновляет открытый инвентарь
         * Возможно кривовато, но ничего лучше я не придумал
         */
        public void refreshInventory() {
            openPage(page, params);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class SongGUIChest implements SongGUIItem {
        private final FullSongContainer container;
    }

    @Getter
    @AllArgsConstructor
    private static class SongGUISong implements SongGUIItem {
        private final MusicBoxSong song;
    }

    private interface SongGUIItem {

    }
}
