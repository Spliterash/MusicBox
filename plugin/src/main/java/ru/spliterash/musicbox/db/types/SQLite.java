package ru.spliterash.musicbox.db.types;

import ru.spliterash.musicbox.db.AbstractBase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends AbstractBase {
    private final File file;

    public SQLite(File file) {
        super("SQLite");
        this.file = file;
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("File write error: " + file);
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("jdbc driver unavailable!");
        }
        afterInit();
    }


    @Override
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + file);

    }
}
