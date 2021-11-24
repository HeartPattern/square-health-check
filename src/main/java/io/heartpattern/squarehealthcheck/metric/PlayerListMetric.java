package io.heartpattern.squarehealthcheck.metric;

import io.heartpattern.squarehealthcheck.event.AsyncHealthCheckEvent;
import io.heartpattern.squarehealthcheck.model.HealthCheckMetric;
import io.heartpattern.squarehealthcheck.model.HealthCheckStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerListMetric implements Listener {
    public static final String NAME = "players";

    private final int warnThreshold;
    private final int errorThreshold;
    private final boolean listPlayers;

    @EventHandler
    private void onHealthCheck(AsyncHealthCheckEvent event) {
        var playerList = Bukkit.getOnlinePlayers();
        var playerCount = playerList.size();

        var status = HealthCheckStatus.FINE;

        if (warnThreshold != -1 && playerCount >= warnThreshold)
            status = HealthCheckStatus.WARNING;

        if (errorThreshold != -1 && playerCount >= errorThreshold)
            status = HealthCheckStatus.ERROR;

        var detail = listPlayers
                ? new PlayerListDetail(
                playerCount,
                playerList.stream().map(Entity::getUniqueId).toList()
        )
                : new PlayerCountDetail(playerCount);

        event.addMetrics(
                new HealthCheckMetric(
                        NAME,
                        status,
                        detail
                )
        );
    }

    @Data
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlayerCountDetail {
        private final int count;
    }

    @Data
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlayerListDetail {
        private final int count;
        private final List<UUID> players;
    }
}
