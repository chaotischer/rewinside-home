package tv.rewinside.home.utils;

import org.bukkit.Bukkit;
import tv.rewinside.home.HomeBukkitPlugin;

import java.util.concurrent.*;

public class ThreadUtil {

    private ThreadPoolExecutor executorService;
    private ScheduledExecutorService scheduledService;

    public void init() {
        this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(2, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(HomeBukkitPlugin.getInstance().getName());
            return new Thread(runnable);
        });
        this.scheduledService = Executors.newScheduledThreadPool(2, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(HomeBukkitPlugin.getInstance().getName());
            return new Thread(runnable);
        });
    }

    public void shutdown(){
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) executorService.shutdownNow();
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public ThreadPoolExecutor getExecutorService() {
        return executorService;
    }

    public ScheduledExecutorService getScheduledService() {
        return scheduledService;
    }
}