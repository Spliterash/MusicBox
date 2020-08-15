package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import ru.spliterash.musicbox.Lang;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
}
