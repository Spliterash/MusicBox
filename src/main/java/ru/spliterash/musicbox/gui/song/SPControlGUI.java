package ru.spliterash.musicbox.gui.song;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.interfaces.MusicBoxSongPlayer;
import ru.spliterash.musicbox.customPlayers.models.MusicBoxSongPlayerModel;
import ru.spliterash.musicbox.minecraft.gui.GUI;
import ru.spliterash.musicbox.minecraft.gui.InventoryAction;
import ru.spliterash.musicbox.minecraft.gui.actions.ClickAction;
import ru.spliterash.musicbox.minecraft.gui.actions.PlayerClickAction;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.ItemUtils;
import ru.spliterash.musicbox.utils.StringUtils;
import ru.spliterash.musicbox.utils.classes.PeekList;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Контроллер SongPlayer'а
 * Включает в себя
 * - Перемотку
 * - Выбор и просмотр треков в текущем плейлисте
 */
public class SPControlGUI {
    private final MusicBoxSongPlayerModel spModel;
    private final GUI gui;
    private final MusicBoxSong currentPlay;
    private boolean closed = false;

    public SPControlGUI(MusicBoxSongPlayerModel songPlayerModel) {
        this.spModel = songPlayerModel;
        this.currentPlay = songPlayerModel.getPlayList().getCurrent();
        this.gui = new GUI(Lang.CONTROL_GUI_TITLE.toString("{song}", currentPlay.getName()), 3);
        refresh();
    }

    public void refresh() {
        IPlayList list = spModel.getPlayList();
        List<MusicBoxSong> prev = list.getPrevSongs(4);
        Collections.reverse(prev);
        List<MusicBoxSong> next = list.getNextSongs(4);
        PeekList<XMaterial> peekList = new PeekList<>(BukkitUtils.DISCS);
        for (ListIterator<MusicBoxSong> iterator = prev.listIterator(); iterator.hasNext(); ) {
            addDiscItem(iterator.nextIndex(), iterator.next(), peekList, false);
        }
        addDiscItem(4, currentPlay, peekList, true);
        for (ListIterator<MusicBoxSong> iterator = next.listIterator(); iterator.hasNext(); ) {
            addDiscItem(5 + iterator.nextIndex(), iterator.next(), peekList, false);
        }
        updateRewind();
    }

    private void addDiscItem(int index, MusicBoxSong song, PeekList<XMaterial> peekList, boolean playNow) {
        gui.addItem(index, song.getSongStack(peekList.getAndNext(), playNow ?
                        Lang.SONG_PANEL_NOW_PLAY.toList() :
                        Lang.SONG_PANEL_SWITH_TO.toList(),
                playNow
        ), new ClickAction(() -> {
            spModel.getPlayList().setSong(song);
            spModel.createNextPlayer();
        }));
    }

    public void openNext(MusicBoxSongPlayerModel nextModel) {
        Set<Player> set = BukkitUtils.findOpenPlayers(gui);
        if (set.size() > 0) {
            SPControlGUI g = nextModel.getControlGUI();
            set.forEach(g::open);
        }
    }

    private void updateRewind() {
        if (closed) {
            return;
        }
        MusicBoxSongPlayer musicPlayer = spModel.getMusicBoxSongPlayer();
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
                    new PlayerClickAction(
                            p -> {
                                musicPlayer.getApiPlayer().setTick(chunkStart);
                                p.sendMessage(Lang.REWINDED.toString(rewindReplaceArray));
                                updateRewind();
                            }
                    )
            );
        }
        // Заполнить всё остальное выходом
    }

    public void openNoRefresh(Player p) {
        gui.open(p);
    }

    public void open(Player p) {
        openNoRefresh(p);
        refresh();
    }

    public void close() {
        closed = true;
        ItemStack close = ItemUtils.createStack(XMaterial.RED_STAINED_GLASS_PANE, Lang.CLOSE.toString(), null);
        InventoryAction action = new PlayerClickAction(HumanEntity::closeInventory);
        for (int i = 0; i < gui.getInventory().getSize(); i++) {
            ItemStack item = gui.getInventory().getItem(i);
            if (item != null)
                continue;
            gui.addItem(i, close, action);
        }
    }
}
