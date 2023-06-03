package com.froobworld.seemore.scheduler;

public interface ScheduledTask {

    void cancel();

    boolean isCancelled();

}
