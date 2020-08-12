package ru.spliterash.musicbox.players;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Класс для хранения настроек игрока
 */
@NoArgsConstructor
@Getter
public class PlayerConfig {
    private static final Yaml yaml = new Yaml();
    private transient boolean change = false;
    private File file;
    /**
     * Громкость
     */
    private byte volume = 100;

    public static PlayerConfig load(File file) {
        try {
            PlayerConfig config;
            if (!file.isFile())
                config = new PlayerConfig();
            else {
                config = yaml.loadAs(new FileInputStream(file), PlayerConfig.class);
            }
            config.file = file;
            return config;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setVolume(byte volume) {
        this.volume = volume;
        change = true;
    }

    public void save() {
        if (change) {
            String dump = yaml.dump(this);
            try {
                FileOutputStream stream = new FileOutputStream(file, false);
                stream.write(dump.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
