package io.heartpattern.squarehealthcheck.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HealthCheckResult {
    private final HealthCheckStatus status;
    private final List<HealthCheckMetric> metrics;
}
