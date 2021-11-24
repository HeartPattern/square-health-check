package io.heartpattern.squarehealthcheck.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadConfig {
    private final int corePoolSize;
    private final int maxPoolSize;
    private final int keepAliveTime;

    public static ThreadConfig parse(ConfigurationSection config) {
        return new ThreadConfig(
                config.getInt("corePoolSize"),
                config.getInt("maxPoolSize"),
                config.getInt("keepAliveTime")
        );
    }
}
