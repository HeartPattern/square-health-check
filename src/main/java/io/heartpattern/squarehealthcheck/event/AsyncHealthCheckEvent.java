package io.heartpattern.squarehealthcheck.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHealthCheckEvent extends HealthCheckEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AsyncHealthCheckEvent() {
        super(true);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
