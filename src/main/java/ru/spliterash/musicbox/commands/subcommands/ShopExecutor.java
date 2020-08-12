package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShopExecutor implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if(args.length==0){
            PlayerWrapper
                    .getInstance(player)
                    .openShopInventory();
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length <= 1) {
            return MusicBoxSongManager
                    .getRootContainer()
                    .getAllSongs()
                    .stream()
                    .map(MusicBoxSong::getName)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
