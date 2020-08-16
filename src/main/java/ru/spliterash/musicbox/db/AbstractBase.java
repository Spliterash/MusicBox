package ru.spliterash.musicbox.db;

import org.intellij.lang.annotations.Language;
import ru.spliterash.musicbox.db.utils.NamedParamStatement;
import ru.spliterash.musicbox.db.utils.ResultSetRow;
import ru.spliterash.musicbox.players.PlayerConfig;
import ru.spliterash.musicbox.customPlayers.playlist.PlayerPlayList;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractBase {
    private final String name;

    protected AbstractBase(String name) {
        this.name = name;
    }

    public List<String> getColumns(ResultSet set) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        List<String> list = new LinkedList<>();
        for (int i = 0; i < meta.getColumnCount(); i++) {
            list.add(meta.getColumnName(i + 1));
        }
        return list;
    }

    public List<ResultSetRow> extractSet(ResultSet set) throws SQLException {
        List<ResultSetRow> list = new LinkedList<>();
        List<String> columns = getColumns(set);
        while (set.next()) {
            ResultSetRow.ResultSetRowBuilder row = ResultSetRow.builder();
            for (String column : columns) {
                row.addResultRow(column, set.getObject(column));
            }
            list.add(row.build());
        }
        return Collections.unmodifiableList(list);
    }

    protected void afterInit() {
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            for (String s : getCreationScript().split(";")) {
                statement.executeUpdate(s);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Language("SQL")
    protected String getCreationScript() {
        try {
            String fileName = name + ".sql";
            String path = "db/" + fileName;
            InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            return StringUtils.getString(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setValue(PreparedStatement statement, int i, T obj) throws SQLException {
        int index = i + 1;
        if (obj instanceof String)
            statement.setString(index, (String) obj);
        else if (obj instanceof Double)
            statement.setDouble(index, (double) obj);
        else if (obj instanceof Integer)
            statement.setInt(index, (Integer) obj);
        else if (obj instanceof Date)
            statement.setDate(index, (Date) obj);
        else if (obj instanceof Long)
            statement.setLong(index, (Long) obj);
        else
            statement.setObject(index, obj);

    }

    protected abstract Connection getConnection() throws SQLException;

    protected PreparedStatement prepare(Connection connection, String query, Object... args) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int i = 0; i < args.length; i++) {
            setValue(statement, i, args[i]);
        }
        return statement;

    }

    protected List<ResultSetRow> query(Connection connection, @Language("SQL") String query, Object... args) throws SQLException {
        try (PreparedStatement prepared = prepare(connection, query, args)) {
            return extractSet(prepared.executeQuery());
        }
    }

    protected List<ResultSetRow> query(@Language("SQL") String query, Object... args) {
        try (Connection connection = getConnection()) {
            return query(connection, query, args);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    protected int update(NamedParamStatement statement) {
        try (Connection connection = getConnection()) {
            return statement.executeUpdate(connection);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    protected int update(@Language("SQL") String query, Object... args) {
        try (Connection connection = getConnection()) {
            return update(connection, query, args);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    protected int update(Connection connection, @Language("SQL") String query, Object... args) throws SQLException {
        try (PreparedStatement prepared = prepare(connection, query, args)) {
            return prepared.executeUpdate();
        }
    }


    private List<ResultSetRow> query(NamedParamStatement statement) {
        try (Connection connection = getConnection()) {
            return extractSet(statement.executeQuery(connection));
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    /**
     * Сохраняет PlayList в базе
     */
    public void savePlayList(PlayerPlayList list) {
        @Language("SQL")
        String query;
        try (Connection connection = getConnection()) {
            boolean createNew = list.getId() == -1;
            if (createNew) {
                query = "INSERT INTO playlists (owner,name) values (:owner,:name)";
            } else {
                query = "UPDATE playlists set name = :name where id = :id";
            }
            NamedParamStatement statement = new NamedParamStatement(query);
            statement.setValue("id", list.getId());
            statement.setValue("owner", list.getOwner().toString());
            statement.setValue("name", list.getName());

            // last row id работает только в одном подключении
            statement.executeUpdate(connection);
            if (createNew) {
                int result = query(connection, "SELECT last_insert_rowid()").get(0).getInt(0);
                list.setId(result);
            } else {
                update(connection, "DELETE from playlist_song where playlists_id = ?", list.getId());
            }
            if (list.getSongs().size() > 0) {
                List<Object[]> argsList = list
                        .getSongs()
                        .stream()
                        .map(MusicBoxSong::getHash)
                        .map(h -> new Object[]{
                                list.getId(),
                                h
                        })
                        .collect(Collectors.toList());
                updateBatch(connection, "INSERT INTO playlist_song (playlists_id, song_hash) values (?,?)", argsList);
            }

        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }

    private int[] updateBatch(Connection connection, @Language("SQL") String query, List<Object[]> argsList) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Object[] objects : argsList) {
                for (int i = 0; i < objects.length; i++) {
                    Object obj = objects[i];
                    setValue(statement, i, obj);
                }
                statement.addBatch();
            }
            return statement.executeBatch();
        }
    }

    public void saveConfig(PlayerConfig playerConfig) {
        // TODO
    }

    public PlayerConfig loadConfig(UUID playerUUID) {
        // TODO
        return new PlayerConfig();
    }
}
