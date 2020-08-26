package ru.spliterash.musicbox.db;


import ru.spliterash.musicbox.MusicBox;
import ru.spliterash.musicbox.db.types.SQLite;

import java.io.File;


public class DatabaseLoader {
    private static DatabaseLoader instance;
    private final AbstractBase base;

    public static void reload() {
        instance = new DatabaseLoader();
    }

    public static AbstractBase getBase() {
        return instance.base;
    }

    private DatabaseLoader() {
        try {
            base = new SQLite(new File(MusicBox.getInstance().getDataFolder(), "base.db"));

        } catch (Exception ex) {
            throw new RuntimeException("Can't connect to db", ex);
        }
    }
}
