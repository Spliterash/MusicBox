package ru.spliterash.musicbox.gui.playlist;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.minecraft.gui.GUI;
import ru.spliterash.musicbox.minecraft.gui.actions.ClickAction;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.ItemUtils;
import ru.spliterash.musicbox.utils.classes.PeekList;

import java.util.Collections;
import java.util.List;

public class PlayListEditorGUI {
    private final PlayerPlayListModel model;
    private final PlayerWrapper wrapper;

    public PlayListEditorGUI(PlayerWrapper wrapper, PlayerPlayListModel model) {
        this.wrapper = wrapper;
        this.model = model;
    }


    public void open(int page) {
        int last = getLastPage();
        GUI gui = new GUI(Lang.PLAYLIST_EDITOR_LIST_TITLE.toString(
                "{playlist}", model.getName(),
                "{page}", String.valueOf(page + 1),
                "{last_page}", String.valueOf(last)
        ));
        gui.open(wrapper.getPlayer());
        List<MusicBoxSong> songs = model.getSongs();
        int start = 45 * page;
        PeekList<XMaterial> list = new PeekList<>(BukkitUtils.DISCS);
        for (int i = 0; i + start < songs.size() && i < 45; i++) {
            int arrayIndex = i + start;
            MusicBoxSong song = songs.get(arrayIndex);
            ItemStack stack = song.getSongStack(list.getAndNext(), Lang.PLAYLIST_ITEM_LORE.toList(), false);
            gui.addItem(i, stack, new ClickAction(
                    () -> wrapper.play(song),
                    () -> {
                        model.getSongs().remove(arrayIndex);
                        open(page);
                    }
            ));
        }
        gui.addItem(
                47,
                ItemUtils.createStack(XMaterial.PISTON, Lang.SHUFFLE_PLAYLIST.toString(), null),
                new ClickAction(
                        () -> {
                            Collections.shuffle(model.getSongs());
                            open(page);
                        }
                )
        );
        gui.addItem(
                48,
                ItemUtils.createStack(XMaterial.SUNFLOWER, Lang.ADD_MUSIC_TO_PLAYLIST_ITEM.toString(), Lang.DONT_FORGET_TO_SAVE.toList()),
                new ClickAction(
                        () -> GUIActions.openPlayListAdder(wrapper, this)
                )
        );
        List<String> lore = null;
        if (model.getSongs().size() == 0) {
            lore = Lang.PLAYLIST_ZERO_SIZE.toList();
        }
        gui.addItem(
                49,
                ItemUtils.createStack(XMaterial.PAPER, Lang.SAVE_PLAYLIST_CHANGE.toString(), lore),
                new ClickAction(
                        () -> {
                            if (model.getSongs().size() > 0) {
                                if (saveInProgress) {
                                    wrapper.getPlayer().sendMessage(Lang.CHILL_CHILL_MAN.toString());
                                } else {
                                    saveInProgress = true;
                                    Bukkit.getScheduler().runTaskAsynchronously(MusicBox.getInstance(), () -> {
                                        model.save();
                                        wrapper.getPlayer().sendMessage(Lang.PLAYLIST_SAVED.toString("{playlist}", model.getName()));
                                        saveInProgress = false;
                                    });
                                }


                            } else {
                                wrapper.getPlayer().sendMessage(Lang.PLAYLIST_ZERO_SIZE.toString());
                            }
                        }
                )
        );
        gui.addItem(
                51,
                ItemUtils.createStack(XMaterial.BARRIER, Lang.DELETE_PLAYLIST.toString("{playlist}", model.getName()), null),
                new ClickAction(
                        () -> {
                            model.delete();
                            wrapper.getPlayer().sendMessage(Lang.PLAYLIST_DELETED.toString("{playlist}", model.getName()));
                            wrapper.getPlayer().closeInventory();
                        }
                )
        );


        if (page > 0)
            gui.addItem(
                    45,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.BACK.toString(), null),
                    new ClickAction(() -> open(page - 1)));
        if ((last - 1) > page)
            gui.addItem(
                    53,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.NEXT.toString(), null),
                    new ClickAction(() -> open(page + 1)));
    }

    private boolean saveInProgress = false;

    private int getLastPage() {
        return (int) Math.ceil(model.getSongs().size() / 45D);
    }

    public void addSong(MusicBoxSong song) {
        List<MusicBoxSong> songs = model.getSongs();
        if (!songs.contains(song))
            songs.add(song);
    }

    public boolean hasSong(MusicBoxSong s) {
        return model.getSongs().contains(s);
    }
}
