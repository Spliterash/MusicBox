package ru.spliterash.musicbox.db;

import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.musicbox.db.model.PlayerPlayListModel;
import ru.spliterash.musicbox.db.utils.NamedParamStatement;
import ru.spliterash.musicbox.db.utils.ResultSetRow;
import ru.spliterash.musicbox.song.MusicBoxSong;
import ru.spliterash.musicbox.song.MusicBoxSongManager;
import ru.spliterash.musicbox.utils.BukkitUtils;
import ru.spliterash.musicbox.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class AbstractBase {
    private final String name;

    protected AbstractBase(String name) {
        this.name = name;
    }

    protected List<String> getColumns(ResultSet set) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        List<String> list = new LinkedList<>();
        for (int i = 0; i < meta.getColumnCount(); i++) {
            list.add(meta.getColumnName(i + 1));
        }
        return list;
    }

    protected List<ResultSetRow> extractSet(ResultSet set) throws SQLException {
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
     * Просто пометка чтобы выполнять большой запрос в асинхронне
     */
    private void largeQuery() {
        if (Bukkit.isPrimaryThread())
            throw new RuntimeException("WTF MAN, it a primary thread");
    }


    /**
     * Сохраняет PlayList в базе
     */
    public void savePlayList(PlayerPlayListModel list) {
        largeQuery();
        @Language("SQL")
        String query;
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
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
                                h,
                                null
                        })
                        .collect(Collectors.toList());
                for (int i = 0; i < argsList.size(); i++) {
                    Object[] array = argsList.get(i);
                    array[2] = i;
                }
                updateBatch(connection, "INSERT INTO playlist_song (playlists_id, song_hash,pos) values (?,?,?)", argsList);
            }
            connection.commit();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }


    }

    private void updateBatch(@Language("SQL") String query, List<Object[]> argsList) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            updateBatch(connection, query, argsList);
            connection.commit();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void updateBatch(Connection connection, @Language("SQL") String query, List<Object[]> argsList) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Object[] objects : argsList) {
                for (int i = 0; i < objects.length; i++) {
                    Object obj = objects[i];
                    setValue(statement, i, obj);
                }
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private List<PlayerPlayListModel> extractPlayList(List<ResultSetRow> set) {
        List<PlayerPlayListModel> list = new LinkedList<>();
        for (ResultSetRow row : set) {
            int id = row.getInt("id");
            PlayerPlayListModel model = list
                    .stream()
                    .filter(l -> l.getId() == id)
                    .findFirst()
                    .orElseGet(() -> {
                        PlayerPlayListModel m = new PlayerPlayListModel(
                                id,
                                UUID.fromString(row.getString("owner")),
                                row.getString("name"));
                        list.add(m);
                        return m;
                    });
            MusicBoxSongManager
                    .findSongByHash(row.getInt("song_hash"))
                    .ifPresent(s -> model.getSongs().add(s));
        }
        list
                .removeIf(l -> {
                    if (l.getSongs().size() == 0) {
                        l.delete();
                        return true;
                    } else
                        return false;
                });
        return list;
    }

    public List<PlayerPlayListModel> getPlayLists(UUID playerUUID) {
        List<ResultSetRow> result = query("SELECT p.id, p.owner,p.name, ps.song_hash\n" +
                "from playlist_song ps\n" +
                "join playlists p on ps.playlists_id = p.id\n" +
                "where p.owner = ?" +
                "order by pos ", playerUUID.toString());

        return extractPlayList(result);
    }

    public void deleteMe(PlayerPlayListModel model) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            update(connection, "DELETE from playlists where id = ?", model.getId());
            update(connection, "DELETE  from playlist_song where playlists_id = ?", model.getId());
            connection.commit();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    @Nullable
    public PlayerPlayListModel getPlayListById(int id) {
        List<ResultSetRow> result = query("SELECT p.id, p.owner,p.name, ps.song_hash\n" +
                "from playlist_song ps\n" +
                "join playlists p on ps.playlists_id = p.id\n" +
                "where p.id = ?" +
                "order by pos ", id);

        List<PlayerPlayListModel> list = extractPlayList(result);
        if (list.size() > 0)
            return list.get(0);
        else
            return null;
    }

    /**
     * Возвращает список сохранённых табличек, и сразу же удаляет его
     */
    public List<Location> getPreventedSigns() {
        try {
            @Cleanup Connection connection = getConnection();
            @Cleanup Statement statement = connection.createStatement();
            @Cleanup ResultSet result = statement.executeQuery("SELECT location FROM signs");
            List<Location> list = new LinkedList<>();
            while (result.next()) {
                Location location = BukkitUtils.parseLocation(result.getString(1));
                if (location != null)
                    list.add(location);
            }
            //noinspection SqlWithoutWhere
            statement.executeUpdate("DELETE FROM signs");
            return list;
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    public void savePreventedSigns(Collection<Location> locations) {
        try {
            @Cleanup
            Connection connection = getConnection();
            connection.setAutoCommit(false);
            List<Object[]> argsList = locations
                    .stream()
                    .map(BukkitUtils::locationToString)
                    .map(str -> new String[]{str})
                    .collect(Collectors.toList());
            updateBatch(connection, "INSERT INTO signs (location) values (?)", argsList);
            connection.commit();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }
}
