package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.gui.GUIActions;
import ru.spliterash.musicbox.players.PlayerWrapper;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.utils.ArrayUtils;
import ru.spliterash.musicbox.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class GiveExecutor implements SubCommand {
    @Override
    public String getPex() {
        return "musicbox.give";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Lang.COMMAND_HELP_GIVE.toString());
        } else {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(Lang.PLAYER_OFLLINE.toString("{player}", args[0]));
                return;
            }
            String songName = args[1].replace('_', ' ');
            MusicBoxSong song = MusicBoxSongManager.findByName(songName).orElse(null);
            if (song != null) {
                GUIActions.giveDisc(PlayerWrapper.getInstance(p), song);
            } else {
                sender.sendMessage(Lang.SONG_NOT_FOUND.toString());
            }
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return null;
        }
        if (args.length == 2) {
            Stream<String> stream = MusicBoxSongManager
                    .getRootContainer()
                    .getAllSongs()
                    .stream()
                    .map(MusicBoxSong::getName)
                    .map(s -> s.replace(' ', '_'));
            return StringUtils.tabCompletePrepare(ArrayUtils.removeFirst(String.class, args), stream);
        }
        return Collections.emptyList();
    }
}
