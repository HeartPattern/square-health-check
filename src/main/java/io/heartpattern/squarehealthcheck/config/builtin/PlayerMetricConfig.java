package io.heartpattern.squarehealthcheck.config.builtin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerMetricConfig {
    private final boolean enabled;
    private final int warnThreshold;
    private final int errorThreshold;
    private final boolean listPlayer;

    @Contract("null -> null; !null -> !null")
    public static @Nullable PlayerMetricConfig parse(@Nullable ConfigurationSection config) {
        if (config == null)
            return null;

        return new PlayerMetricConfig(
                config.getBoolean("enabled"),
                config.getInt("warnThreshold"),
                config.getInt("errorThreshold"),
                config.getBoolean("listPlayer")
        );
    }
}
