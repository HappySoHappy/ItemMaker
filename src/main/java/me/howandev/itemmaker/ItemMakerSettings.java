package me.howandev.itemmaker;

import me.howandev.itemmaker.configuration.impl.Configuration;
import org.jetbrains.annotations.NotNull;

public class ItemMakerSettings {
    private final @NotNull Configuration config;
    public ItemMakerSettings(final @NotNull Configuration config) {
        this.config = config;
    }

    public boolean perPlayerLocale() {
        return config.getBoolean("translation.per-player-locale", false);
    }
}
