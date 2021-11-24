package io.heartpattern.squarehealthcheck.metric;

import io.heartpattern.squarehealthcheck.event.AsyncHealthCheckEvent;
import io.heartpattern.squarehealthcheck.model.HealthCheckMetric;
import io.heartpattern.squarehealthcheck.model.HealthCheckStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Add metric based on TPS
 */
@RequiredArgsConstructor
public class TpsMetric implements Listener {
    public static final String NAME = "tps";

    private final int warningThreshold;
    private final int errorThreshold;

    @EventHandler
    private void onHealthCheck(AsyncHealthCheckEvent event) {
        var tps = Bukkit.getTPS();

        var status = HealthCheckStatus.FINE;

        if (warningThreshold != -1 && tps[0] <= warningThreshold)
            status = HealthCheckStatus.WARNING;

        if (warningThreshold != -1 && tps[0] <= errorThreshold)
            status = HealthCheckStatus.ERROR;

        var detail = new Detail(tps[0], tps[1], tps[2]);

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
    public static class Detail {
        private final double oneMinute;
        private final double fiveMinute;
        private final double fifteenMinute;
    }
}
