package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.playlist.ListPlaylist;
import ru.spliterash.musicbox.song.MusicBoxSongManager;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class SignUtils {
    public Optional<IPlayList> parseSignPlaylist(Sign sign) {
        String lineTwo = sign.getLine(1);
        if (!StringUtils.strip(lineTwo).equalsIgnoreCase("[music]"))
            return Optional.empty();
        String songId = sign.getLine(0);
        boolean rand = sign.getLine(3).contains("R");
        return MusicBoxSongManager
                .getContainerById(StringUtils.strip(songId))
                .map(c -> ListPlaylist.fromContainer(c, rand, hasEnd(sign)));
    }

    public boolean hasEnd(Sign sign) {
        return !sign.getLine(3).contains("E");
    }

    public int parseSignRange(Sign sign) {
        int range;
        try {
            range = Integer.parseInt(sign.getLine(2));
            if (range > 256)
                range = 256;
        } catch (Exception exception) {
            range = 24;
        }
        return range;
    }

    // Ищет ближайшую табличку не считая этот location
    public Optional<Sign> findSign(Location startLoc) {
        BukkitUtils.checkPrimary();
        Location location = startLoc.clone();
        for (int i = 0; i < 5; i++) {
            location.add(0, 1, 0);
            Block block = location.getBlock();
            if (block.getState() instanceof Sign) {
                return Optional.of((Sign) block.getState());
            }
        }
        location = startLoc.clone();
        for (int i = 0; i < 5; i++) {
            location.add(0, -1, 0);
            Block block = location.getBlock();
            if (block.getState() instanceof Sign) {
                return Optional.of((Sign) block.getState());
            }
        }
        return Optional.empty();
    }

    public void setSignList(Sign sign, List<String> list) {

        for (int i = 0; i < 4; i++) {
            String str = list.size() > i ? list.get(i) : "";
            sign.setLine(i, str);
        }
        sign.update();
    }

    /**
     * Выводит плейлист на табло
     */
    public void setPlayListInfo(Location signLocation, IPlayList list) {
        BukkitUtils.checkPrimary();
        Block b = signLocation.getBlock();
        if (b.getState() instanceof Sign) {
            Sign sign = (Sign) b.getState();
            List<String> signText = SongUtils.generatePlaylistLore(list, 1, 2);
            setSignList(sign, signText);
        }
    }

    /**
     * Устанавливает на табличке что проигрыватель выключен
     */
    public void setPlayerOff(Location signLocation) {
        BukkitUtils.checkPrimary();
        Block b = signLocation.getBlock();
        if (b.getState() instanceof Sign) {
            Sign sign = (Sign) b.getState();
            List<String> playerOff = Lang.INFO_SIGN_OFF.toList();
            setSignList(sign, playerOff);
        }
    }
}
