package ru.spliterash.musicbox.gui;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.song.songContainers.containers.FolderSongContainer;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
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
import ru.spliterash.musicbox.song.songContainers.containers.SingletonContainer;
import ru.spliterash.musicbox.utils.EconomyUtils;
import ru.spliterash.musicbox.utils.ItemUtils;
import ru.spliterash.musicbox.song.songContainers.SongContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

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
            // Перемотка
            defaultBar[0] = rewindButton();
            // Кнопка остановки
            defaultBar[1] = stopButton();
            // Место для паузы

            // Редактор плейлистов
            defaultBar[3] = playListEditor();
            // Кнопка для следующей музыки из плейлиста
            defaultBar[4] = nextPlaylistSong();
            // Смена режима проигрывания
            defaultBar[5] = switchPlayMode();
            DEFAULT_MODE = SongGUIParams
                    .builder()
                    .onSongLeftClick(GUIActions::playerPlayMusic)
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

    private BarButton nextPlaylistSong() {
        return new BarButton() {
            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                PlayerSongPlayer player = wrapper.getActivePlayer();
                if (player == null)
                    return null;
                IPlayList playlist = player.getPlayList();
                if (!playlist.hasNext())
                    return null;
                List<String> lore = new ArrayList<>(7);
                for (MusicBoxSong song : playlist.getPreviousSong(3)) {
                    lore.add(Lang.ANOTHER_PLAYLIST_SONG.toString(
                            "{song}", song.getName(),
                            "{num}", String.valueOf(playlist.getSongNum(song) + 1)
                    ));
                }
                lore.add(Lang.CURRENT_PLAYLIST_SONG.toString(
                        "{song}", playlist.getCurrent().getName(),
                        "{num}", String.valueOf(playlist.getSongNum(playlist.getCurrent()) + 1)
                ));
                for (MusicBoxSong song : playlist.getNextSongs(3)) {
                    lore.add(Lang.ANOTHER_PLAYLIST_SONG.toString(
                            "{song}", song.getName(),
                            "{num}", String.valueOf(playlist.getSongNum(song) + 1)
                    ));
                }
                lore.addAll(Lang.PLAYLIST_CONTROL.toList());
                return ItemUtils.createStack(
                        XMaterial.REDSTONE,
                        Lang.NEXT_PLAYLIST_SONG_TITLE.toString(),
                        lore
                );
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return e -> {
                    PlayerSongPlayer player = wrapper.getActivePlayer();
                    if (player == null)
                        return;
                    switch (e.getClick()) {
                        case LEFT:
                            player.destroy();
                            data.refreshInventory();
                            break;
                        case RIGHT:
                            player.getPlayList().back(2);
                            player.destroy();
                            data.refreshInventory();
                            break;
                    }

                };
            }
        };
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
                return e -> openPlaylistListEditor(wrapper);
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
     * TODO
     * Открывает редактор плейлиста
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
                return e -> {
                    if (wrapper.switchModeChecked()) {
                        data.refreshInventory();
                    }
                };
            }
        };
    }

    private BarButton rewindButton() {
        return new BarButton() {
            private final ItemStack rewindItem = ItemUtils.createStack(XMaterial.REPEATER, Lang.REWIND_BUTTON.toString(), null);

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                return wrapper.getActivePlayer() != null ? rewindItem : null;
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return e -> {
                    PlayerSongPlayer active = wrapper.getActivePlayer();
                    if (active == null) {
                        wrapper.getPlayer().sendMessage(Lang.NOT_PLAY.toString());
                    } else {
                        active.getRewind().openForPlayer(wrapper.getPlayer());
                    }
                };
            }


        };
    }

    private BarButton stopButton() {
        return new BarButton() {
            private final ItemStack stopItem = ItemUtils.createStack(XMaterial.BARRIER, Lang.SONG_STOP.toString(), null);

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                return stopItem;
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return e -> {
                    wrapper.destroyActivePlayer();
                    data.refreshInventory();
                };
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
                return e -> editorGUI.open(0);
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
     * Только плейлисты
     *
     * @param wrapper Игрок который настраивает
     * @param sign    Настраиваемая табличка
     */
    public void openSignSetupInventory(PlayerWrapper wrapper, Sign sign) {
        SongContainerGUI rootGUI = MusicBoxSongManager.getRootContainer().createGUI(wrapper);
        class RandButton implements BarButton {
            private boolean rand = true;

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                String status = rand ? Lang.ENABLE.toString() : Lang.DISABLE.toString();
                return ItemUtils.createStack(
                        XMaterial.REDSTONE,
                        Lang.RANDOM_MODE_BUTTON.toString("{status}", status),
                        null);
            }

            @Override
            public InventoryAction getAction(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                return e -> {
                    rand = !rand;
                    data.refreshInventory();
                };
            }
        }
        RandButton button = new RandButton();
        BarButton[] buttons = new BarButton[3];
        buttons[2] = button;
        SongGUIParams params = SongGUIParams
                .builder()
                .bottomBar(buttons)
                .onSongLeftClick(
                        (wrapper1, musicBoxSongSongGUIData) ->
                                applySign(
                                        wrapper1,
                                        sign,
                                        new SingletonContainer(musicBoxSongSongGUIData.getData()),
                                        button.rand)
                )
                .onContainerRightClick(
                        (wrapper12, musicBoxSongContainerSongGUIData) ->
                                applySign(
                                        wrapper12,
                                        sign,
                                        new FolderSongContainer(musicBoxSongContainerSongGUIData.getData()),
                                        button.rand)
                )
                .extraSongLore(nothing -> Lang.SIGN_SONG_LORE.toList())
                .extraContainerLore(nothing -> Lang.SIGN_CHEST_LORE.toList())
                .build();
        rootGUI.openPage(0, params);
    }

    private List<String> signLore(PlayerPlayListModel model) {
        return Lang.SIGN_PLAYLIST_LORE.toList();
    }


    private void applySign(PlayerWrapper wrapper, Sign sign, SongContainer songContainer, boolean rand) {
        sign.setLine(0, ChatColor.AQUA + songContainer.getNameId());
        sign.setLine(1, String.format("%s[%sMUSIC%s]", ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY));
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
        if (rand)
            sign.setLine(3, "RAND");
        else
            sign.setLine(3, "");
        sign.update(true);
        wrapper.getPlayer().closeInventory();
    }
}
