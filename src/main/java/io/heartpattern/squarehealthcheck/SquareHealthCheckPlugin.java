package io.heartpattern.squarehealthcheck;

import com.sun.net.httpserver.HttpServer;
import io.heartpattern.squarehealthcheck.config.BuiltInConfig;
import io.heartpattern.squarehealthcheck.config.SquareHealthCheckConfig;
import io.heartpattern.squarehealthcheck.metric.HeartBeatMetric;
import io.heartpattern.squarehealthcheck.metric.PlayerListMetric;
import io.heartpattern.squarehealthcheck.metric.TpsMetric;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SquareHealthCheckPlugin extends JavaPlugin {
    private HttpServer httpServer;
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        var config = loadConfig();

        registerBuiltInMetrics(config.getBuildIn());
        startServer(config);
    }

    @Override
    public void onDisable() {
        httpServer.stop(0);
        threadPoolExecutor.shutdown();
        try {
            boolean result = threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS);
            if (!result)
                getLogger().log(Level.WARNING, "ThreadPoolExecutor won't terminate in 30 seconds.");
        } catch (InterruptedException e) {
            getLogger().log(Level.WARNING, "ThreadPoolExecutor terminate exceptionally", e);
        }
    }

    private void startServer(SquareHealthCheckConfig config) {
        threadPoolExecutor = new ThreadPoolExecutor(
                config.getThread().getCorePoolSize(),
                config.getThread().getMaxPoolSize(),
                config.getThread().getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        try {
            httpServer = HttpServer.create();

            var bindAddress = config.getBindAddress().isEmpty()
                    ? new InetSocketAddress(config.getPort())
                    : new InetSocketAddress(config.getBindAddress(), config.getPort());

            httpServer.bind(bindAddress, config.getBacklog());
            httpServer.createContext(config.getPath(), new HealthCheckHandler(getLogger(), this, threadPoolExecutor));
            httpServer.setExecutor(threadPoolExecutor);

            httpServer.start();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Exception thrown while starting http server", e);
        }
    }

    private void registerBuiltInMetrics(@Nullable BuiltInConfig config) {
        if (config == null)
            return;

        if (config.getHeartBeat() != null && config.getHeartBeat().isEnabled())
            Bukkit.getPluginManager().registerEvents(new HeartBeatMetric(), this);

        if (config.getPlayer() != null && config.getPlayer().isEnabled()) {
            var metricConfig = config.getPlayer();
            Bukkit.getPluginManager().registerEvents(
                    new PlayerListMetric(
                            metricConfig.getWarnThreshold(),
                            metricConfig.getErrorThreshold(),
                            metricConfig.isListPlayer()
                    ),
                    this
            );
        }

        if (config.getTps() != null && config.getTps().isEnabled()) {
            var metricConfig = config.getTps();
            Bukkit.getPluginManager().registerEvents(
                    new TpsMetric(
                            metricConfig.getWarnThreshold(),
                            metricConfig.getErrorThreshold()
                    ),
                    this
            );
        }
    }

    private SquareHealthCheckConfig loadConfig() {
        var config = getConfig();

        return SquareHealthCheckConfig.parse(config);
    }
}
