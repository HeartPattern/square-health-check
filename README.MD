# Square Health Check

Square Health Check is minecraft health check plugin based on http and bukkit event system.

## Configuration
```yaml
bindAddress: string # Bind address for http server. Leave it empty to allow all address
port: number # Port where http server serve.
backlog: number # Backlog of tcp connection
path: string # Path for health check endpoint
thread: # Thread configuration of http worker thread
  corePoolSize: number # Core pool size of http worker thread
  maxPoolSize: number # Max pool size of http worker thread
  keepAliveTime: number # Keep alive time of http worker thread
builtIn: # Built-in metrics configuration
  heartBeat: # Sync http response to main thread heartbeat. If this metrics is on, you will get timeout for health check if server stuck in deadlock. 
    enabled: boolean
  player: # Metrics based on online player count
    enabled: boolean
    warnThreshold: number # Minimum number of player to make health check status warning
    errorThreshold: number # Minimum number of player to make health check status error
    listPlayer: boolean # Whether to include online player uuid list on metrics detail.
  tps: #Metrics based on tps (Paper only)
    enabled: boolean
    warnThreshold: number # Maximum value of tps to make health check status warning
    errorThreshold: number # Maximum value of tps to make health check status error
```

### Example
```yaml
bindAddress: ''
port: 8080
backlog: 128
path: /
thread:
  corePoolSize: 1
  maxPoolSize: 8
  keepAliveTime: 300
builtIn:
  heartBeat:
    enabled: true
  player:
    enabled: true
    warnThreshold: 80
    errorThreshold: 100
    listPlayer: true
  tps:
    enabled: true
    warnThreshold: 18
    errorThreshold: 15
```

## Developing custom metrics
You can add customer metrics via bukkit event system. \
SquareHealthCheck provide two event, `AsyncHealthCheckEvent` and `SyncHealthCheckEvent`
which is invoked in http worker thread and main thread respectively. \
It is recommended to use async if possible to make http response faster. 

### Example
Following code implement custom metrics based on number of entity in world.
```java
public class EntityHealthCheck implements Listener {
    public static final String NAME = "entity";
    
    @EventHandler
    private void onHealthCheck(AsyncHealthCheckEvent event) {
        int entityCount = Bukkit.getWorlds()
                .stream()
                .mapToInt(World::getEntityCount)
                .sum();
        
        Detail detail = new Detail(entityCount);
        HealthCheckStatus status = HealthCheckStatus.FINE;
        
        if (entityCount >= 1000)
            status = HealthCheckStatus.WARNING;
        
        if (entityCount >= 1500)
            status = HealthCheckStatus.ERROR;
        
        event.addMetrics(
                new HealthCheckMetric(
                        NAME,
                        status,
                        detail
                )
        );
    }
    
    private static class Detail {
        private final int entityCount;
        
        public int getEntityCount() {
            return entityCount;
        }
        
        private Detail(int entityCount) {
            this.entityCount = entityCount;
        }
    }
}

```
