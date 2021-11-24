package io.heartpattern.squarehealthcheck.event;

import io.heartpattern.squarehealthcheck.model.HealthCheckMetric;
import org.bukkit.event.Event;

import java.util.LinkedList;

public abstract class HealthCheckEvent extends Event {
    private LinkedList<HealthCheckMetric> metrics = new LinkedList<>();

    HealthCheckEvent(boolean isAsync) {
        super(isAsync);
    }

    public void addMetrics(HealthCheckMetric metric) {
        this.metrics.add(metric);
    }

    public LinkedList<HealthCheckMetric> getMetrics() {
        return metrics;
    }
}
