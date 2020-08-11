package ru.spliterash.musicbox;

import lombok.Getter;
@Getter
public class MusicBoxConfig {

    private BossBarSetting bossbar;

    @Getter
    public static class BossBarSetting {
        private boolean enable;
        private String color;
        private String style;

    }
}
