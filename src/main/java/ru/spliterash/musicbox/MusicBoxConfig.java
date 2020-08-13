package ru.spliterash.musicbox;

import lombok.Getter;
import ru.spliterash.musicbox.utils.EconomyUtils;

@Getter
public class MusicBoxConfig {

    private BossBarSetting bossbar;
    private HoloSetting holo;
    private EconomySetting economy;

    @Getter
    public static class BossBarSetting {
        private boolean enable;
        private String color;
        private String style;

    }

    @Getter
    public static class HoloSetting {
        private boolean enable;
        private double height;
    }

    @Getter
    public static class EconomySetting {
        private boolean enable;
        private double price;
    }
}
