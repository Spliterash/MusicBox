package ru.spliterash.musicbox.gui.song;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.customPlayers.interfaces.PlayerSongPlayer;
import ru.spliterash.musicbox.gui.RewindGUI;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongContainer;
import ru.spliterash.musicbox.utils.EconomyUtils;
import ru.spliterash.musicbox.utils.ItemUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Класс для больших кусков кода связанных с GUI
 */
@UtilityClass
public class GUIMode {


    public final SongContainerGUI.SongGUIParams DEFAULT_MODE;
    public final SongContainerGUI.SongGUIParams SHOP_MODE;

    static {
        // Стандартный режим
        {
            SongContainerGUI.BarButton[] defaultBar = new SongContainerGUI.BarButton[6];
            defaultBar[0] = rewindButton();
            //Кнопка остановки
            defaultBar[2] = stopButton();
            DEFAULT_MODE = SongContainerGUI.SongGUIParams
                    .builder()
                    .onSongLeftClick(GUIMode::playerPlayMusic)
                    .bottomBar(defaultBar)
                    .build();
        }
        // Покупка пластинок
        {
            SHOP_MODE = SongContainerGUI.SongGUIParams
                    .builder()
                    .onSongLeftClick(GUIMode::playerBuyMusic)
                    .extraSongLore(GUIMode::playerBuySongLore)
                    .extraContainerLore(GUIMode::playerBuyAllContainerLore)
                    .onContainerRightClick(GUIMode::buyAllContainer)
                    .build();
        }
    }

    private static SongContainerGUI.BarButton rewindButton() {
        return new SongContainerGUI.BarButton() {
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

    private SongContainerGUI.BarButton stopButton() {
        return new SongContainerGUI.BarButton() {
            private final ItemStack stopItem = ItemUtils.createStack(XMaterial.BARRIER, Lang.SONG_STOP.toString(), null);

            @Override
            public ItemStack getItemStack(PlayerWrapper wrapper) {
                return stopItem;
            }

            @Override
            public void processClick(PlayerWrapper wrapper, SongContainerGUI.SongGUIData<Void> data) {
                wrapper.stopPlay();
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
        player.play(data.getData());
        data.refreshInventory();
    }

    public List<String> addContainerToPlaylist(SongContainerGUI.SongGUIData<MusicBoxSongContainer> container) {
        return Lang.ADD_CONTAINER_TO_PLAYLIST.toList();
    }


    public List<String> addMusicToPlaylistLore(SongContainerGUI.SongGUIData<MusicBoxSong> song) {
        return Lang.ADD_MUSIC_TO_PLAYLIST.toList();
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
    private void playerBuyMusic(PlayerWrapper wrapper, MusicBoxSong musicBoxSong) {
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
}
