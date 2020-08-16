package ru.spliterash.musicbox.db.utils;

import org.intellij.lang.annotations.Language;
import ru.spliterash.musicbox.db.AbstractBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NamedParamStatement {
    private static final Pattern findParametersPattern = Pattern.compile("(?<!')(:[\\w]*)(?!')");
    private final String query;
    private final Map<String, Collection<?>> map = new HashMap<>();

    public NamedParamStatement(@Language("SQL") String query) {
        this.query = query;
    }

    private PreparedStatement prepareStatement(Connection connection) throws SQLException {
        Matcher matcher = findParametersPattern.matcher(query);
        final String finalQuery;
        List<Object> result = new ArrayList<>(map.size());
        StringBuilder queryBuilder = new StringBuilder();
        int oldEnd = 0;
        while (matcher.find()) {
            queryBuilder.append(query, oldEnd, matcher.start());
            oldEnd = matcher.end();
            String key = matcher.group().substring(1);
            Collection<?> set = map.get(key);
            StringBuilder replaceTo = new StringBuilder();
            if (set != null && set.size() > 0) {
                result.addAll(set);
                for (int i = 0; i < set.size(); i++) {
                    replaceTo.append('?');
                    if (i < set.size() - 1)
                        replaceTo.append(',');
                }
            } else {
                replaceTo.append('?');
                result.add(null);
            }
            queryBuilder.append(replaceTo);
        }
        // Если не было ничо
        if (oldEnd == 0)
            finalQuery = query;
        else
            finalQuery = queryBuilder.append(query, oldEnd, query.length()).toString();
        PreparedStatement statement = connection.prepareStatement(finalQuery);
        for (int i = 0; i < result.size(); i++) {
            AbstractBase.setValue(statement, i, result.get(i));
        }
        reset();
        return statement;
    }

    public ResultSet executeQuery(Connection connection) throws SQLException {
        return prepareStatement(connection).executeQuery();
    }

    public int executeUpdate(Connection connection) throws SQLException {
        return prepareStatement(connection).executeUpdate();
    }

    /**
     * Очищает карту и можно пихать следующие данные
     */
    public void reset() {
        map.clear();
    }

    public void setValue(String key, Object value) {
        map.put(key, Collections.singleton(value));
    }


    public <T> void setValues(String key, Collection<T> values) {
        map.put(key, values);
    }

    public <T> void setValues(String key, T[] values) {
        Set<T> set = Arrays.stream(values).collect(Collectors.toSet());
        map.put(key, set);
    }
}