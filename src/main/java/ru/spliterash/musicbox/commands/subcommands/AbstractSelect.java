package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.utils.StringUtils;

import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractSelect implements SubCommand {

    private final String pex;

    public AbstractSelect(String pex) {
        this.pex = pex;
    }

    @Override
    public String getPex() {
        return pex;
    }

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
        return StringUtils.tabCompletePrepare(args, stream);
    }
}
