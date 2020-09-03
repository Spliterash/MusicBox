package ru.spliterash.musicbox.customPlayers.objects.jukebox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.minecraft.nms.jukebox.JukeboxCustom;
import ru.spliterash.musicbox.minecraft.nms.jukebox.JukeboxFactory;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.utils.FaceUtils;
import ru.spliterash.musicbox.utils.ItemUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Реализация плейлиста для Jukebox'а, благодаря чему будет работать весь функионал с плейлистами
 * ООП все дела
 */
class JukeboxPlaylistImpl implements IPlayList {

    private final Location jukeboxLoc;

    public JukeboxPlaylistImpl(Location jukebox) throws JukeboxPlaylistInitException {
        this.jukeboxLoc = jukebox;
        @Nullable Inventory inv = getChestInventory();
        Jukebox j = getJukebox();
        if (j == null)
            throw new JukeboxPlaylistInitException("Location does not contains jukebox");
        if (inv != null) {
            ItemUtils.groupInventory(inv);
        }
        JukeboxCustom jc = JukeboxFactory.getJukebox(j);
        MusicBoxSong startSongTemp;
        if (jc.isEmpty()) {
            if (inv != null) {
                ChestIndex s = getByIndex(inv, 0);
                if (s == null) {
                    startSongTemp = null;
                } else {
                    startSongTemp = s.getSong();
                    inv.setItem(s.getIndex(), null);
                    jc.setJukebox(s.getStack());
                    ItemUtils.groupInventory(inv);
                }
            } else
                startSongTemp = null;
        } else {
            startSongTemp = MusicBoxSongManager.findByItem(jc.getJukebox()).orElse(null);
        }
        if (startSongTemp == null)
            throw new JukeboxPlaylistInitException("Song is empty");
    }

    private Jukebox getJukebox() {
        Block b = jukeboxLoc.getBlock();
        if (b.getState() instanceof Jukebox)
            return (Jukebox) b.getState();
        else
            return null;
    }

    @Nullable
    private Inventory getChestInventory() {
        Jukebox box = getJukebox();
        if (box == null)
            return null;
        @NotNull Block b = box.getBlock();
        Chest chest = FaceUtils.getRelativeAround(b, Chest.class);
        if (chest == null)
            return null;
        return chest.getInventory();
    }

    private void swapItems(Supplier<ChestIndex> nextItemGetter, Supplier<Integer> putToIndexGetter) {
        @Nullable Inventory inv = getChestInventory();
        if (inv == null)
            return;
        swapItems(inv, nextItemGetter, putToIndexGetter);
    }

    private void swapItems(Inventory inv, Supplier<ChestIndex> nextItemGetter, Supplier<Integer> putToIndexGetter) {
        Jukebox box = getJukebox();
        if (box == null)
            return;
        JukeboxCustom cBox = JukeboxFactory.getJukebox(box);
        ChestIndex nextItem = nextItemGetter.get();
        if (nextItem == null)
            return;
        inv.setItem(nextItem.getIndex(), null);
        ItemStack currentJukeboxItem = cBox.getJukebox();
        if (currentJukeboxItem != null) {
            int puttingIndex = putToIndexGetter.get();
            if (puttingIndex > -1) {
                inv.setItem(puttingIndex, currentJukeboxItem);
            } else {
                //noinspection ConstantConditions
                jukeboxLoc.getWorld().dropItem(jukeboxLoc, currentJukeboxItem);
            }
        }
        cBox.setJukebox(nextItem.getStack());
        ItemUtils.groupInventory(inv);
    }

    @Override
    public void next() {
        @Nullable Inventory inv = getChestInventory();
        if (inv != null)
            swapItems(
                    inv,
                    () -> getByIndex(inv, 0),
                    () -> getLastFreeIndex(inv));
    }

    private void next(Inventory inv) {
        swapItems(
                inv,
                () -> getByIndex(inv, 0),
                () -> getLastFreeIndex(inv));
    }

    private void back(Inventory inventory) {
        swapItems(
                () -> getLastSong(inventory),
                () -> {
                    ItemUtils.groupInventory(inventory);
                    ItemUtils.shiftInventory(inventory, -1);
                    return 0;
                }
        );
    }

    @Override
    public List<MusicBoxSong> getNextSongs(int count) {
        @Nullable Inventory inv = getChestInventory();
        if (inv == null)
            return Collections.emptyList();
        int endIndex = Math.min(count, inv.getSize());
        List<MusicBoxSong> list = new LinkedList<>();
        int c = 0;
        for (int i = 0; i < endIndex; i++) {
            ChestIndex item = getByIndex(inv, i);
            if (item != null) {
                list.add(item.getSong());
                if (++c >= count)
                    break;
            }
        }
        return list;
    }

    @Override
    public List<MusicBoxSong> getPrevSongs(int count) {
        @Nullable Inventory inv = getChestInventory();
        if (inv == null)
            return Collections.emptyList();
        List<MusicBoxSong> list = new LinkedList<>();
        int startIndex = getLastFreeIndex(inv) - 1;
        if (startIndex <= -1)
            return Collections.emptyList();
        int c = 0;
        for (int i = startIndex; i >= 0; i--) {
            ChestIndex item = getByIndex(inv, i);
            if (item != null) {
                list.add(item.getSong());
                if (++c >= count)
                    break;
            }
        }
        return list;
    }

    @Override
    public boolean hasNext() {
        @Nullable Inventory inv = getChestInventory();
        if (inv == null)
            return false;
        else
            return hasNext(inv);
    }

    private ChestIndex getByIndex(Inventory inventory, int index) {
        if (index >= inventory.getSize())
            return null;
        ItemStack item = inventory.getItem(index);
        if (item == null || item.getType().equals(Material.AIR))
            return null;
        return MusicBoxSongManager
                .findByItem(item)
                .map(a -> new ChestIndex(index, item, a))
                .orElse(null);
    }

    private boolean hasNext(Inventory inv) {
        ItemUtils.groupInventory(inv);
        return getByIndex(inv, 0) != null;
    }

    @Override
    public boolean hasPrev() {
        @Nullable Inventory inv = getChestInventory();
        if (inv == null)
            return false;
        else
            return hasPrev(inv);
    }

    private int getLastFreeIndex(Inventory inventory) {
        for (int i = inventory.getSize() - 1; i >= 0; i--) {
            @Nullable ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().equals(Material.AIR))
                return i;
        }
        return -1;
    }

    private ChestIndex getLastSong(Inventory inventory) {
        ItemUtils.groupInventory(inventory);
        for (int i = inventory.getSize() - 1; i >= 0; i--) {
            ChestIndex chestIndex = getByIndex(inventory, i);
            if (chestIndex != null)
                return chestIndex;
        }
        return null;
    }

    private boolean hasPrev(Inventory inventory) {
        return getLastSong(inventory) != null;
    }

    @Override
    public MusicBoxSong getCurrent() {
        Jukebox j = getJukebox();
        if (j == null)
            return null;
        ItemStack item = JukeboxFactory.getJukebox(j).getJukebox();
        if (item == null || item.getType().equals(Material.AIR))
            return null;
        return MusicBoxSongManager.findByItem(item).orElse(null);
    }

    @Override
    public void back(int count) {
        @Nullable Inventory inv = getChestInventory();
        if (inv != null)
            for (int i = 0; i < count; i++) {
                back(inv);
            }
    }

    @Override
    public int getSongNum(MusicBoxSong song) {
        return -1;
    }


    @Override
    public void setSong(MusicBoxSong song) {
        // Чтобы лишний раз не мотать, проверим есть ли такой айтем в сундуке или он уже стоит
        // Юзаю равно, потому что это полюбому одни и те же объекты
        if (getCurrent() == song)
            return;
        @Nullable Inventory inv = getChestInventory();
        if (inv == null)
            return;
        int songIndex = ItemUtils.findItem(inv, stack -> song == MusicBoxSongManager.findByItem(stack).orElse(null));
        // Какая то зараза достала из сундука
        if (songIndex == -1)
            return;
        int limiter = 0;
        while (getCurrent() != song && limiter++ < inv.getSize()) {
            next(inv);
        }

    }

    @Getter
    @AllArgsConstructor
    private static class ChestIndex {
        private final int index;
        private final ItemStack stack;
        private final MusicBoxSong song;
    }
}
