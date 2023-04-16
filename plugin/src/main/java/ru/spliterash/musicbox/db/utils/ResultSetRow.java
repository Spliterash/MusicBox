package ru.spliterash.musicbox.db.utils;

import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class ResultSetRow {
    @Singular("addResultRow")
    private final Map<String, Object> result;

    public Integer getInt(String key) {
        return (Integer) result.get(key);
    }

    public String getString(String key) {
        return result.get(key).toString();
    }

    public Integer getInt(int i) {
        int k = -1;
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            if (++k == i) {
                return (Integer) entry.getValue();
            }
        }
        return null;
    }
}
