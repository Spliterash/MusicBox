package ru.spliterash.musicbox.players;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import ru.spliterash.musicbox.db.DatabaseLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Класс для хранения настроек игрока
 */
@NoArgsConstructor
@Getter
public class PlayerConfig {
    private transient boolean change = false;
    /**
     * Громкость
     */
    private byte volume = 100;

    public void setVolume(byte volume) {
        this.volume = volume;
        change = true;
    }

    public void save() {
        if (change) {
            DatabaseLoader.getBase().saveConfig(this);
        }
    }

}
