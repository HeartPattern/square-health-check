package io.heartpattern.squarehealthcheck.model;

import lombok.Getter;

@Getter
public enum HealthCheckStatus {
    FINE(0, 200),
    WARNING(1000, 200),
    ERROR(2000, 500);

    private int priority;
    private int responseCode;

    HealthCheckStatus(int priority, int responseCode) {
        this.priority = priority;
        this.responseCode = responseCode;
    }
}
