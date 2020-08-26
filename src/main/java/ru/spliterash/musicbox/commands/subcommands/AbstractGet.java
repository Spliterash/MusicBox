package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractGet implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            noArgs(player);
        } else {
            String songName = args[0].replace('_', ' ');
            MusicBoxSong song = MusicBoxSongManager.findByName(songName).orElse(null);
            if (song != null) {
                processSong(player, song);
            } else {
                player.sendMessage(Lang.SONG_NOT_FOUND.toString());
            }
        }
    }

    protected abstract void noArgs(Player player);

    protected abstract void processSong(Player player, MusicBoxSong song);

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        Stream<String> stream = MusicBoxSongManager
                .getRootContainer()
                .getAllSongs()
                .stream()
                .map(MusicBoxSong::getName)
                .map(s -> s.replace(' ', '_'));
        if (args.length < 1) {
            return stream.collect(Collectors.toList());
        } else if (args.length == 1) {
            String start = args[0].toLowerCase();
            return stream
                    .filter(s -> s.toLowerCase().startsWith(start))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
