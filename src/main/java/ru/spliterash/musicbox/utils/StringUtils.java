package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import ru.spliterash.musicbox.Lang;
import sun.security.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class StringUtils {
    public String getOrEmpty(String title, Supplier<String> getName) {
        if (title == null || title.isEmpty())
            return getName.get();
        else
            return title;
    }

    public String t(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public List<String> t(Collection<String> collection) {
        return collection
                .stream()
                .map(StringUtils::t)
                .collect(Collectors.toList());
    }

    public String replace(String source, String... replace) {
        if (replace.length > 0) {
            if (replace.length % 2 != 0)
                throw new RuntimeException("Oooooooooops");
            String str = source;
            for (int i = 1; i < replace.length; i = i + 2) {
                if (i % 2 == 1)
                    str = str.replace(replace[i - 1], replace[i]);
            }
            return str;
        } else {
            return source;
        }

    }

    public String toHumanTime(int second) {
        int min = (int) Math.floor((double) second / 60D);
        int sec = second % 60;
        String result = "";
        if (min > 0) {
            result = Lang.HUMAN_TIME_MINUTE.toString("{value}", String.valueOf(min)) + " ";
        }
        result += Lang.HUMAN_TIME_SECOND.toString("{value}", String.valueOf(sec));
        return result;
    }

    public String getString(InputStream stream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try (Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            while (true) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            return out.toString();
        }
    }

    public String concat(String[] array, int start, int end) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            String element = array[i];
            builder.append(element);
            if (i < end - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public String strip(String str) {
        return ChatColor.stripColor(str);
    }

    @NotNull
    public static List<String> tabCompletePrepare(String[] args, Stream<String> stream) {
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
