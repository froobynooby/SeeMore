package com.froobworld.seemore.scheduler;

import com.froobworld.seemore.SeeMore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class BukkitSchedulerHook implements SchedulerHook {
    private final SeeMore seeMore;

    public BukkitSchedulerHook(SeeMore seeMore) {
        this.seeMore = seeMore;
    }

    @Override
    public ScheduledTask runTask(Runnable runnable) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTask(seeMore, runnable).getTaskId());
    }

    @Override
    public ScheduledTask runTaskDelayed(Runnable runnable, long delay) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLater(seeMore, runnable, delay).getTaskId());
    }

    @Override
    public ScheduledTask runRepeatingTask(Runnable runnable, long initDelay, long period) {
        return new BukkitScheduledTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(seeMore, runnable, initDelay, period));
    }

    @Override
    public ScheduledTask runEntityTask(Runnable runnable, Runnable retired, Entity entity) {
        return runTask(runnable);
    }

    @Override
    public ScheduledTask runEntityTaskAsap(Runnable runnable, Runnable retired, Entity entity) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return new ScheduledTask() {
                @Override
                public void cancel() {}

                @Override
                public boolean isCancelled() {
                    return false;
                }
            };
        }
        return runTask(runnable);
    }

    private static class BukkitScheduledTask implements ScheduledTask {
        private final int taskId;

        private BukkitScheduledTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void cancel() {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        @Override
        public boolean isCancelled() {
            return !Bukkit.getScheduler().isQueued(taskId) && !Bukkit.getScheduler().isCurrentlyRunning(taskId);
        }
    }

}
