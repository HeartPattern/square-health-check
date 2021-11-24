package io.heartpattern.squarehealthcheck.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SquareHealthCheckConfig {
    private final String bindAddress;
    private final int port;
    private final int backlog;
    private final String path;
    private final ThreadConfig thread;
    @Nullable
    private final BuiltInConfig buildIn;

    public static SquareHealthCheckConfig parse(ConfigurationSection config) {
        return new SquareHealthCheckConfig(
                config.getString("bindAddress"),
                config.getInt("port"),
                config.getInt("backLog"),
                config.getString("path"),
                ThreadConfig.parse(Objects.requireNonNull(config.getConfigurationSection("thread"))),
                BuiltInConfig.parse(config.getConfigurationSection("builtIn"))
        );
    }
}
