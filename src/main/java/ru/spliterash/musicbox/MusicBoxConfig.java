package ru.spliterash.musicbox;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.InputStream;

@SuppressWarnings("unused")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MusicBoxConfig {
    public static MusicBoxConfig parseConfig(InputStream yamlStream) {
        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(MusicBoxConfig.class, MusicBoxConfig.class.getClassLoader()));
        yaml.setBeanAccess(BeanAccess.FIELD);
        return yaml.loadAs(yamlStream, MusicBoxConfig.class);
    }

    private boolean printNewInstrument;
    private EconomySetting economy;
    private BossBarSetting bossbar;
    private HoloSetting holo;
    private int speakerRadius;
    private int jukeboxRadius;
    private String lang;
    private int autoDestroy;
    private boolean bStats;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BossBarSetting {
        private boolean enable;
        private String color;
        private String style;

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class HoloSetting {
        private boolean enable;
        private double height;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EconomySetting {
        private boolean enable;
        private double price;
    }
}
