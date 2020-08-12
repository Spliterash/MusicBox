package ru.spliterash.musicbox;

import lombok.Getter;

@Getter
public class MusicBoxConfig {

    private BossBarSetting bossbar;
    private HoloSetting holo;

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
}
