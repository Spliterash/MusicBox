package ru.spliterash.musicbox.gui;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.objects.SignPlayer;
import ru.spliterash.musicbox.customPlayers.playlist.ListPlaylist;
import ru.spliterash.musicbox.customPlayers.playlist.SingletonPlayList;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.gui.playlist.PlayListEditorGUI;
import ru.spliterash.musicbox.gui.playlist.PlayListListGUI;
import ru.spliterash.musicbox.gui.song.SongContainerGUI;
import ru.spliterash.musicbox.minecraft.gui.InventoryAction;
import ru.spliterash.musicbox.minecraft.gui.actions.ClickAction;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongContainer;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.song.songContainers.SongContainer;
import ru.spliterash.musicbox.song.songContainers.containers.SingletonContainer;
import ru.spliterash.musicbox.utils.EconomyUtils;
import ru.spliterash.musicbox.utils.ItemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.spliterash.musicbox.gui.song.SongContainerGUI.BarButton;
import static ru.spliterash.musicbox.gui.song.SongContainerGUI.SongGUIParams;

/**
 * Класс для больших кусков кода связанных с GUI
 */
@UtilityClass
public class GUIActions {


    public SongGUIParams GET_MODE;
    public SongGUIParams DEFAULT_MODE;
    public SongGUIParams SHOP_MODE;

    public void reloadGUI() {
        // Стандартный режим
        {
            BarButton[] defaultBar = new BarButton[6];
            // Кнопка открытия панели
            defaultBar[0] = controlPanelButton();
            // Кнопка остановки, просто чтобы не лезть каждый раз в панель
            defaultBar[2] = stopButton();
            // Редактор плейлистов
            defaultBar[4] = playListEditor();
            // Смена режима проигрывания
            defaultBar[5] = switchPlayMode();
            DEFAULT_MODE = SongGUIParams
                    .builder()
                    .onSongLeftClick(GUIActions::playerPlayMusic)
                    .onContainerRightClick(GUIActions::playContainer)
                    .extraContainerLore(GUIActions::playerPlayAllContainer)
                    .bottomBar(defaultBar)
                    .build();
        }
        // Покупка пластинок
        {
            SHOP_MODE = SongGUIParams
                    .builder()
                    .onSongLeftClick(GUIActions::playerBuyMusic)
                    .extraSongLore(GUIActions::playerBuySongLore)
                    .extraContainerLore(GUIActions::playerBuyAllContainerLore)
                    .onContainerRightClick(GUIActions::buyAllContainer)
                    .build();
        }
        // Получение пластинок
        {
            GET_MODE = SongGUIParams
                    .builder()
                    .onSongLeftClick(GUIActions::giveDisc)
                    .extraSongLore(GUIActions::playerGetSongLore)
                    .onContainerRightClick(GUIActions::getAllContainer)
                    .extraContainerLore(GUIActions::playerGetAllContainerLore)
                    .build();
        }
    }

    private static List<String> playerPlayAllContainer(SongContainerGUI.SongGUIData<MusicBoxSongContainer> data) {
        return Lang.CLICK_TO_PLAY_CONTAINER.toList();
    }

    private static void playContainer(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<MusicBoxSongContainer> data) {
        wrapper.play(data.getData());
        data.refreshInventory();
    }

    private List<String> playerGetAllContainerLore(SongContainerGUI.SongGUIData<MusicBoxSongContainer> musicBoxSongContainerSongGUIData) {
        return Lang.GET_ALL_CONTAINER_LORE.toList();
    }

    private List<String> playerGetSongLore(SongContainerGUI.SongGUIData<MusicBoxSong> data) {
        return Lang.GET_DISC_LORE.toList();
    }

    private void getAllContainer(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<MusicBoxSongContainer> data) {
        for (MusicBoxSong song : data.getData().getAllSongs()) {
            giveDisc(wrapper, song);
        }
    }

    private void giveDisc(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<MusicBoxSong> data) {
        giveDisc(wrapper, data.getData());
    }

    private BarButton playListEditor() {
        return new BarButton() {
            private final ItemStack item = ItemUtils.createStack(XMaterial.PAPER, Lang.PLAYLIST_EDITOR.toString(), null);

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                return item;
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return new ClickAction(() -> openPlaylistListEditor(wrapper));
            }

        };
    }

    public void openPlaylistListEditor(PlayerWrapper wrapper) {
        List<String> list = Lang.DEFAULT_PLAYLIST_LORE.toList();
        new PlayListListGUI(wrapper).openPage(0,
                model -> new ClickAction(
                        () -> wrapper.play(ListPlaylist.fromContainer(model, false, false)),
                        () -> {
                            if (model instanceof PlayerPlayListModel) {
                                openPlaylistEditor(wrapper, (PlayerPlayListModel) model);
                            }
                        }
                ),
                model -> list);
    }

    /**
     * Открывает редактор плейлиста
     * re
     *
     * @param wrapper Игрок
     * @param model   Его плейлист
     */
    public void openPlaylistEditor(PlayerWrapper wrapper, PlayerPlayListModel model) {
        new PlayListEditorGUI(wrapper, model).open(0);
    }

    private BarButton switchPlayMode() {
        return new BarButton() {
            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                List<String> lore;
                if (wrapper.canSwitch()) {
                    String status = wrapper.isSpeaker() ? Lang.ENABLE.toString() : Lang.DISABLE.toString();
                    lore = Lang.SWITH_MODE_LORE.toList("{status}", status);
                } else
                    lore = Lang.SWITH_MODE_NO_PEX_LORE.toList();
                return ItemUtils.createStack(XMaterial.NOTE_BLOCK, Lang.SPEAKER_MODE.toString(), lore);
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return new ClickAction(() -> {
                    if (wrapper.switchModeChecked()) {
                        data.refreshInventory();
                    }
                });
            }
        };
    }

    private BarButton controlPanelButton() {
        return new BarButton() {
            private final ItemStack rewindItem = ItemUtils.createStack(XMaterial.REPEATER, Lang.CONTROL_PANEL_BUTTON.toString(), null);

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                return wrapper.getActivePlayer() != null ? rewindItem : null;
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return new ClickAction(() -> {
                    PlayerSongPlayer active = wrapper.getActivePlayer();
                    if (active == null) {
                        wrapper.getPlayer().sendMessage(Lang.NOT_PLAY.toString());
                    } else {
                        active.getControl().open(wrapper.getPlayer());
                    }
                });
            }


        };
    }

    public ItemStack getStopStack() {
        return ItemUtils.createStack(XMaterial.BARRIER, Lang.SONG_STOP.toString(), null);
    }

    public BarButton stopButton() {
        return new BarButton() {
            private final ItemStack stopItem = getStopStack();

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                return wrapper.isPlayNow() ? stopItem : null;
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return new ClickAction(() -> {
                    wrapper.destroyActivePlayer();
                    data.refreshInventory();
                });
            }

        };
    }


    /**
     * Вызывается когда игрок хочет просто послушать музыку
     *
     * @param player Сам игрок
     * @param data   Данные в которых есть музыка
     */
    public void playerPlayMusic(PlayerWrapper player, SongContainerGUI.SongGUIData<MusicBoxSong> data) {
        player.play(new SingletonPlayList(data.getData()));
        data.refreshInventory();
    }


    public List<String> playerBuySongLore(SongContainerGUI.SongGUIData<MusicBoxSong> musicBoxSong) {
        return Lang.BUY_MUSIC_LORE.toList("{price}", String.valueOf(EconomyUtils.getDiscPrice()));
    }

    /**
     * Процесс покупки для инвентаря
     *
     * @param data Данные о клике, в котором есть покупаемая мелодия
     */
    public void playerBuyMusic(PlayerWrapper player, SongContainerGUI.SongGUIData<MusicBoxSong> data) {
        playerBuyMusic(player, data.getData());
    }

    /**
     * Сам процесс покупки
     *
     * @param wrapper      Игрок который покупает пластинку
     * @param musicBoxSong Сама музыка которую он покупает
     */
    public void playerBuyMusic(PlayerWrapper wrapper, MusicBoxSong musicBoxSong) {
        double price = EconomyUtils.getDiscPrice();
        Player player = wrapper.getPlayer();
        if (EconomyUtils.canBuy(player, price)) {
            HashMap<Integer, ItemStack> result = player.getInventory().addItem(musicBoxSong.getSongStack());
            if (result.size() > 0) {
                player.sendMessage(Lang.NO_INVENTORY_SPACE.toString());
            } else {
                EconomyUtils.buyNoMessage(player, price);
                player.sendMessage(Lang.DISC_BUYED.toString("{disc}", musicBoxSong.getName()));
            }
        }
    }

    public List<String> playerBuyAllContainerLore(SongContainerGUI.SongGUIData<MusicBoxSongContainer> containerData) {
        return Lang.BUY_CONTAINER_LORE.toList(
                "{price}",
                String.valueOf(EconomyUtils.getDiscPrice() * containerData.getData().getAllSongs().size())
        );
    }

    public void buyAllContainer(PlayerWrapper player, SongContainerGUI.SongGUIData<MusicBoxSongContainer> container) {
        for (MusicBoxSong song : container.getData().getAllSongs()) {
            playerBuyMusic(player, song);
        }
    }

    /**
     * Открывает инвентарь для добавления новых мелодий в плейлист
     */
    public void openPlayListAdder(PlayerWrapper wrapper, PlayListEditorGUI editorGUI) {
        @Nullable BarButton[] bar = new BarButton[3];
        bar[2] = new BarButton() {
            private final ItemStack stack = ItemUtils.createStack(XMaterial.BEACON, Lang.GO_BACK_TO_PLAYLIST.toString(), null);

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                return stack;
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return new ClickAction(() -> editorGUI.open(0));
            }
        };
        SongGUIParams params = SongGUIParams.builder()
                .extraContainerLore(s -> Lang.ADD_CONTAINER_TO_PLAYLIST.toList())
                .extraSongLore(s -> {
                    if (editorGUI.hasSong(s.getData()))
                        return Lang.CURRENT_IN_PLAYLIST.toList();
                    else
                        return Lang.ADD_MUSIC_TO_PLAYLIST.toList();
                })
                .onSongLeftClick((w, s) -> {
                    editorGUI.addSong(s.getData());
                    s.refreshInventory();
                })
                .onContainerRightClick((w, c) -> {
                    c.getData().getAllSongs().forEach(editorGUI::addSong);
                    c.refreshInventory();
                })
                .bottomBar(bar)
                .build();
        new SongContainerGUI(MusicBoxSongManager.getRootContainer(), wrapper).openPage(0, params);
    }

    public void giveDisc(PlayerWrapper wrapper, MusicBoxSong song) {
        Player player = wrapper.getPlayer();
        player.getInventory().addItem(song.getSongStack());
        player.sendMessage(Lang.YOU_GET_DISC.toString("{disc}", song.getName()));
    }

    /**
     * Открыть инвентарь для выбора музыки
     */
    public void openDefaultInventory(PlayerWrapper wrapper) {
        SongContainerGUI gui = MusicBoxSongManager.getRootContainer().createGUI(wrapper);
        gui.openPage(0, GUIActions.DEFAULT_MODE);
    }

    /**
     * Открыть инвентарь для покупки пластинок
     */
    public void openShopInventory(PlayerWrapper wrapper) {
        SongContainerGUI gui = MusicBoxSongManager.getRootContainer().createGUI(wrapper);
        gui.openPage(0, GUIActions.SHOP_MODE);
    }

    public void openGetInventory(PlayerWrapper wrapper) {
        SongContainerGUI gui = MusicBoxSongManager.getRootContainer().createGUI(wrapper);
        gui.openPage(0, GUIActions.GET_MODE);
    }

    /**
     * Открывает инвентарь для настройки табличек
     *
     * @param wrapper Игрок который настраивает
     * @param sign    Настраиваемая табличка
     */
    public void openSignSetupInventory(PlayerWrapper wrapper, Sign sign) {
        SongContainerGUI rootGUI = MusicBoxSongManager.getRootContainer().createGUI(wrapper);
        abstract class BooleanButton implements BarButton {
            private final String key;
            private boolean value = false;

            BooleanButton(String key) {
                this.key = key;
            }

            public String getValue() {
                return value ? key : null;
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return new ClickAction(() -> {
                    value = !value;
                    data.refreshInventory();
                });
            }
        }
        /*
          Наличие буквы обозначает что это включенно
          R - Рандомный режим
          I - Поиск инфотаблички включён
          E - Музон будет идти бесконечно
          P - Будет ли табличка оставаться даже если её никто не слышит(так же сохраняет в базу)
         */
        BooleanButton randButton = new BooleanButton("R") {
            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                String status = super.value ? Lang.ENABLE.toString() : Lang.DISABLE.toString();
                return ItemUtils.createStack(
                        XMaterial.REDSTONE,
                        Lang.RANDOM_MODE_BUTTON.toString("{status}", status),
                        null);
            }
        };
        BooleanButton infoSignButton = new BooleanButton("I") {
            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                String status = super.value ? Lang.ENABLE.toString() : Lang.DISABLE.toString();
                return ItemUtils.createStack(
                        XMaterial.OAK_SIGN,
                        Lang.SEARCH_INFO_SIGN_TITLE.toString("{status}", status),
                        Lang.SEARCH_INFO_SIGN_HOVER.toList());
            }
        };
        BooleanButton endlessSign = new BooleanButton("E") {
            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                String status = super.value ? Lang.ENABLE.toString() : Lang.DISABLE.toString();
                return ItemUtils.createStack(
                        XMaterial.GHAST_TEAR,
                        Lang.ENDLESS_SIGN_MODE.toString("{status}", status),
                        null);
            }
        };
        BooleanButton preventDestroy;

        preventDestroy = new BooleanButton("P") {
            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                String status = super.value ? Lang.ENABLE.toString() : Lang.DISABLE.toString();
                return ItemUtils.createStack(
                        XMaterial.CLOCK,
                        Lang.PREVENT_DESTROY_TITLE.toString("{status}", status),
                        Lang.PREVENT_DESTROY_LORE.toList()
                );
            }
        };
        BarButton[] buttons = new BarButton[5];
        if (wrapper.getPlayer().hasPermission("musicbox.admin"))
            buttons[0] = preventDestroy;
        buttons[1] = endlessSign;
        buttons[2] = infoSignButton;
        buttons[3] = randButton;
        Supplier<String> signParams = () ->
                Stream.of(endlessSign, infoSignButton, randButton, preventDestroy)
                        .map(BooleanButton::getValue)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("|"));
        buttons[4] = new BarButton() {
            private final ItemStack item = ItemUtils.createStack(XMaterial.PAPER, Lang.PLAYLIST_EDITOR.toString(), null);

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                return item;
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return new ClickAction(() ->
                        new PlayListListGUI(wrapper).openPage(0, container -> e1 ->
                                applySign(wrapper, sign, container, signParams), a -> Lang.SIGN_CONTAINER_LORE.toList()));
            }
        };
        SongGUIParams params = SongGUIParams
                .builder()
                .bottomBar(buttons)
                .onSongLeftClick(
                        (wrapper1, musicBoxSongSongGUIData) ->
                                applySign(
                                        wrapper1,
                                        sign,
                                        new SingletonContainer(musicBoxSongSongGUIData.getData()),
                                        signParams)
                )
                .onContainerRightClick(
                        (wrapper12, musicBoxSongContainerSongGUIData) ->
                                applySign(
                                        wrapper12,
                                        sign,
                                        musicBoxSongContainerSongGUIData.getData(),
                                        signParams)
                )
                .extraSongLore(nothing -> Lang.SIGN_SONG_LORE.toList())
                .extraContainerLore(nothing -> Lang.SIGN_CONTAINER_LORE.toList())
                .build();
        rootGUI.openPage(0, params);
    }


    private void applySign(PlayerWrapper wrapper, Sign sign, SongContainer songContainer, Supplier<String> params) {
        sign.setLine(0, ChatColor.AQUA + songContainer.getNameId());
        sign.setLine(1, SignPlayer.SIGN_SECOND_LINE);
        String range = sign.getLine(2);
        if (range.isEmpty())
            range = "24";
        else {
            try {
                int rangeInt = Integer.parseInt(range);
                if (rangeInt > 256) {
                    rangeInt = 256;
                }
                range = String.valueOf(rangeInt);
            } catch (Exception ex) {
                range = "24";
            }
        }
        sign.setLine(2, ChatColor.RED + range);
        sign.setLine(3, ChatColor.YELLOW + params.get());
        sign.update(true);
        wrapper.getPlayer().closeInventory();
    }
}
