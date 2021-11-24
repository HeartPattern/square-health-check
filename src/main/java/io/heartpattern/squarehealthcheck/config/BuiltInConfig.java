package io.heartpattern.squarehealthcheck.config;

import io.heartpattern.squarehealthcheck.config.builtin.HeartBeatMetricConfig;
import io.heartpattern.squarehealthcheck.config.builtin.PlayerMetricConfig;
import io.heartpattern.squarehealthcheck.config.builtin.TpsMetricConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BuiltInConfig {
    @Nullable
    private final HeartBeatMetricConfig heartBeat;
    @Nullable
    private final PlayerMetricConfig player;
    @Nullable
    private final TpsMetricConfig tps;

    @Contract("null -> null; !null -> !null")
    static @Nullable BuiltInConfig parse(@Nullable ConfigurationSection config) {
        if (config == null)
            return null;

        return new BuiltInConfig(
                HeartBeatMetricConfig.parse(config.getConfigurationSection("heartBeat")),
                PlayerMetricConfig.parse(config.getConfigurationSection("player")),
                TpsMetricConfig.parse(config.getConfigurationSection("tps"))
        );
    }
}
