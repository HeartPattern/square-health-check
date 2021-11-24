package io.heartpattern.squarehealthcheck.metric;

import io.heartpattern.squarehealthcheck.event.SyncHealthCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This class does not actually add metric.
 * It just ensures health check handler wait for main thread heartbeat.
 */
public class HeartBeatMetric implements Listener {
    @EventHandler
    private void onHealthCheck(SyncHealthCheckEvent event) {
        // Do nothing
    }
}
