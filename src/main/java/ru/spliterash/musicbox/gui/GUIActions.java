package ru.spliterash.musicbox.gui;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.customPlayers.playlist.ListPlaylist;
import ru.spliterash.musicbox.customPlayers.playlist.SingletonPlayList;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.gui.playlist.PlayListEditorGUI;
import ru.spliterash.musicbox.gui.playlist.PlayListListGUI;
import ru.spliterash.musicbox.gui.song.SongContainerGUI;
import ru.spliterash.musicbox.minecraft.gui.actions.ClickAction;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongContainer;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.utils.EconomyUtils;
import ru.spliterash.musicbox.utils.ItemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                @Nullable MusicBoxSong next = playlist.silentGetNext();
                if (next == null)
                    return null;
                List<String> lore = new ArrayList<>(7);
                for (MusicBoxSong song : playlist.getPreviousSong(3)) {
                    lore.add(Lang.ANOTHER_PLAYLIST_SONG.toString("{song}", song.getName()));
                }
                lore.add(Lang.CURRENT_PLAYLIST_SONG.toString("{song}", playlist.getCurrent().getName()));
                for (MusicBoxSong song : playlist.getNextSongs(3)) {
                    lore.add(Lang.ANOTHER_PLAYLIST_SONG.toString("{song}", song.getName()));
                }
                return ItemUtils.createStack(
                        XMaterial.REDSTONE, Lang.NEXT_PLAYLIST_SONG_TITLE.toString(),
                        lore
                );
            }

            @Override
            public void processClick(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                PlayerSongPlayer player = wrapper.getActivePlayer();
                if (player == null)
                    return;
                player.destroy();
                data.refreshInventory();
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
            public void processClick(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                List<String> list = Lang.DEFAULT_PLAYLIST_LORE.toList();
                new PlayListListGUI(wrapper).openPage(0,
                        model -> new ClickAction(
                                () -> wrapper.play(new ListPlaylist(model)),
                                () -> openPlaylistEditor(wrapper, model)
                        ),
                        model -> list);
            }
        };
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
            public void processClick(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                if (wrapper.switchModeChecked()) {
                    data.refreshInventory();
                }
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
            public void processClick(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                PlayerSongPlayer active = wrapper.getActivePlayer();
                if (active == null) {
                    wrapper.getPlayer().sendMessage(Lang.NOT_PLAY.toString());
                } else {
                    active.getRewind().openForPlayer(wrapper.getPlayer());
                }
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
            public void processClick(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                wrapper.destroyActivePlayer();
                data.refreshInventory();
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
            public void processClick(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                editorGUI.open(0);
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
}
