package ru.spliterash.musicbox.gui.playlist;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.db.DatabaseLoader;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.minecraft.gui.GUI;
import ru.spliterash.musicbox.minecraft.gui.InventoryAction;
import ru.spliterash.musicbox.minecraft.gui.actions.ClickAction;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.utils.ItemUtils;
import ru.spliterash.musicbox.utils.StringUtils;
import ru.spliterash.musicbox.song.songContainers.types.SongContainer;

import java.util.List;
import java.util.function.Function;

/**
 * Класс для создания инвентаря со списком плейлистов
 */
public class PlayListListGUI {
    private final List<PlayerPlayListModel> list;
    private final PlayerWrapper wrapper;

    public PlayListListGUI(PlayerWrapper wrapper) {
        this.wrapper = wrapper;
        this.list = DatabaseLoader.getBase().getPlayLists(wrapper.getPlayer().getUniqueId());
    }

    /**
     * Собсна открывает игроку инвентарь
     *
     * @param onSelect - Что делать с выбранном плейлистом(если null то это master плейлист)
     */
    public void openPage(int page, Function<SongContainer, InventoryAction> onSelect, Function<PlayerPlayListModel, List<String>> extraLore) {
        int offset = page * 45;
        int lastPage = getLastPage();
        GUI gui = new GUI(Lang.PLAYLIST_LIST_TITLE.toString(
                "{page}", String.valueOf(page + 1),
                "{last_page}", String.valueOf(lastPage)
        ));
        gui.open(wrapper.getPlayer());
        for (int i = 0; i < 45; i++) {
            int listIndex = offset + i;
            if (listIndex >= list.size())
                break;
            PlayerPlayListModel element = list.get(listIndex);
            List<String> lore = Lang.PLAYLIST_LORE.toList(
                    "{count}", String.valueOf(element.getSongs().size()),
                    "{duration}", StringUtils.toHumanTime(element
                            .getSongs()
                            .stream()
                            .mapToInt(MusicBoxSong::getDuration)
                            .sum())
            );
            if (extraLore != null)
                lore.addAll(extraLore.apply(element));
            ItemStack stack = ItemUtils.createStack(
                    XMaterial.PAPER,
                    Lang.PLAYLIST_NAME.toString("{name}", element.getName()),
                    lore);
            gui.addItem(i, stack, onSelect.apply(element));
        }
        // Создать новый плейлист
        gui.addItem(
                49,
                ItemUtils.createStack(XMaterial.SUGAR, Lang.CREATE_NEW_PLAYLIST.toString(), null),
                new ClickAction(() -> {
                    Player p = wrapper.getPlayer();
                    p.closeInventory();
                    p.sendMessage(Lang.NEW_PLAYLIST_MESSAGE.toString());
                })
        );
        // Главный плейлист
        gui.addItem(
                46,
                ItemUtils.createStack(XMaterial.DIAMOND, Lang.MASTER_PLAYLIST.toString(), Lang.MASTER_PLAYLIST_LORE.toList()),
                onSelect.apply(MusicBoxSongManager.getMasterContainer())
        );
        // Пагинация
        if (page > 0)
            gui.addItem(
                    45,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.BACK.toString(), null),
                    new ClickAction(() -> openPage(page - 1, onSelect, extraLore)));
        if ((lastPage - 1) > page)
            gui.addItem(
                    53,
                    ItemUtils.createStack(XMaterial.MAGMA_CREAM, Lang.NEXT.toString(), null),
                    new ClickAction(() -> openPage(
                            page + 1, onSelect, extraLore)));
    }

    private int getLastPage() {
        return (int) Math.ceil(list.size() / 45D);
    }
}
