package io.heartpattern.squarehealthcheck.config.builtin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HeartBeatMetricConfig {
    private final boolean enabled;

    @Contract("null -> null; !null -> !null")
    public static @Nullable HeartBeatMetricConfig parse(@Nullable ConfigurationSection config) {
        if (config == null)
            return null;

        return new HeartBeatMetricConfig(
                config.getBoolean("enabled")
        );
    }
}
