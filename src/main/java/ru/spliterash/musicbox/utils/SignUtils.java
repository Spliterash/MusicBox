package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import ru.spliterash.musicbox.customPlayers.interfaces.IPlayList;
import ru.spliterash.musicbox.customPlayers.playlist.ListPlaylist;
import ru.spliterash.musicbox.song.MusicBoxSongManager;

import java.util.Optional;

@UtilityClass
public class SignUtils {
    public Optional<IPlayList> parseSignPlaylist(Sign sign, boolean hasEnd) {
        String lineTwo = sign.getLine(1);
        if (!StringUtils.strip(lineTwo).equalsIgnoreCase("[music]"))
            return Optional.empty();
        String songId = sign.getLine(0);
        boolean rand = sign.getLine(3).contains("RAND");
        return MusicBoxSongManager
                .getContainerById(StringUtils.strip(songId))
                .map(c -> ListPlaylist.fromContainer(c, rand, hasEnd));
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

    // Обновляет табличку проигрывания
    public Optional<Sign> findSign(Location startLoc) {
        BukkitUtils.checkPrimary();
        Block block = startLoc.getBlock();
        boolean found = true;
        blockSearch:
        {
            for (int i = 0; i < 5; i++) {
                block = block.getRelative(BlockFace.UP);
                if (block.getState() instanceof Sign) {
                    break blockSearch;
                }
            }
            block = startLoc.getBlock();
            for (int i = 0; i < 5; i++) {
                block.getRelative(BlockFace.DOWN);
                if (block.getState() instanceof Sign) {
                    break blockSearch;
                }
            }
            found = false;
        }
        if (!found)
            return Optional.empty();
        else
            return Optional.of((Sign) block.getState());
    }

    /**
     * Выводит плейлист на табло
     */
    public void setPlayListInfo(Sign s, IPlayList list) {
        BukkitUtils.checkPrimary();
    }

    /**
     * Устанавливает на табличке что проигрыватель выключен
     */
    public void setPlayerOff(Sign sign) {
        BukkitUtils.checkPrimary();
    }
}
