package io.heartpattern.squarehealthcheck.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HealthCheckMetric {
    private final String name;
    private final HealthCheckStatus status;
    private final Object detail;
}
