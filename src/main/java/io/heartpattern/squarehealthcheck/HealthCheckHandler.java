package io.heartpattern.squarehealthcheck;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.heartpattern.squarehealthcheck.event.AsyncHealthCheckEvent;
import io.heartpattern.squarehealthcheck.event.HealthCheckEvent;
import io.heartpattern.squarehealthcheck.event.SyncHealthCheckEvent;
import io.heartpattern.squarehealthcheck.model.HealthCheckMetric;
import io.heartpattern.squarehealthcheck.model.HealthCheckResult;
import io.heartpattern.squarehealthcheck.model.HealthCheckStatus;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class HealthCheckHandler implements HttpHandler {
    private final Logger logger;
    private final Plugin plugin;
    private final ThreadPoolExecutor executor;
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET") || !exchange.getRequestURI().getPath().equals("/")) {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
            return;
        }

        var asyncEvent = new AsyncHealthCheckEvent();
        asyncEvent.callEvent();

        if (SyncHealthCheckEvent.getHandlerList().getRegisteredListeners().length > 0) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                var syncEvent = new SyncHealthCheckEvent();
                syncEvent.callEvent();

                executor.execute(() -> {
                    response(exchange, asyncEvent, syncEvent);
                });
            });
        } else {
            response(exchange, asyncEvent, null);
        }
    }

    private void response(
            HttpExchange exchange,
            AsyncHealthCheckEvent asyncEvent,
            @Nullable HealthCheckEvent syncEvent
    ) {
        int metricsCount = asyncEvent.getMetrics().size() +
                (syncEvent != null ? syncEvent.getMetrics().size() : 0);

        var metrics = new ArrayList<HealthCheckMetric>(metricsCount);
        metrics.addAll(asyncEvent.getMetrics());
        if (syncEvent != null) metrics.addAll(syncEvent.getMetrics());

        var status = HealthCheckStatus.FINE;

        for (var metric : metrics)
            if (metric.getStatus().getPriority() > status.getPriority())
                status = metric.getStatus();

        var response = new HealthCheckResult(status, metrics);

        try (exchange) {
            exchange.sendResponseHeaders(
                    status.getResponseCode(),
                    0
            );
            var responseStream = new BufferedWriter(
                    new OutputStreamWriter(
                            exchange.getResponseBody()
                    )
            );
            responseStream.write(gson.toJson(response));
            responseStream.close();
        } catch (Exception e) {
            logger.log(
                    Level.SEVERE,
                    "Exception thrown while respond to health check",
                    e
            );
        }
    }
}
