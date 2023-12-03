package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.commands.SubCommand;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractSelect implements SubCommand {

    private final String pex;

    public AbstractSelect(String pex) {
        this.pex = pex;
    }
    @Override
    public boolean canExecute(CommandSender sender) {
        return sender.hasPermission(pex);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player)
                noArgs(sender, (Player) sender);
            else
                sender.sendMessage(Lang.SPECIFY_PLAYER.toString());
            return;
        } else if (args.length == 1) {
            if (!sender.hasPermission("musicbox.admin")) {
                sender.sendMessage(Lang.NO_PEX.toString());
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Lang.PLAYER_OFLLINE.toString("{player}", args[0]));
                return;
            }

            noArgs(sender, target);
            return;
        }

        String songName = args[1].replace('_', ' ');
        MusicBoxSong song = MusicBoxSongManager.findByName(songName).orElse(null);
        if (song == null) {
            sender.sendMessage(Lang.SONG_NOT_FOUND.toString());
            return;
        }
        Player target;
        if (!sender.hasPermission("musicbox.admin")) {
            sender.sendMessage(Lang.NO_PEX.toString());
            return;
        }

        String playerName = args[0];
        target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(Lang.PLAYER_OFLLINE.toString("{player}", playerName));
            return;
        }

        processSong(sender, target, song, args);
    }


    protected abstract void noArgs(CommandSender sender, Player player);

    protected abstract void processSong(CommandSender sender, Player target, MusicBoxSong song, String[] args);

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("musicbox.admin")) return Collections.emptyList();

        if (args.length <= 1)
            return StringUtils.tabCompletePrepare(args, Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName));
        else if (args.length <= 2) {
            Stream<String> stream = MusicBoxSongManager
                    .getRootContainer()
                    .getAllSongs()
                    .stream()
                    .map(MusicBoxSong::getName)
                    .map(s -> s.replace(' ', '_'));
            return StringUtils.tabCompletePrepare(args, 2, stream);
        } else
            return Collections.emptyList();
    }
}
