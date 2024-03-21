package com.froobworld.seemore.metrics;

import com.froobworld.seemore.SeeMore;
import com.froobworld.seemore.metrics.charts.NumberOfWorldsChart;

public class SeeMoreMetrics {
    private final Metrics metrics;

    public SeeMoreMetrics(SeeMore seeMore) {
        this.metrics = new Metrics(seeMore, 18658, seeMore.getSchedulerHook()::runTask);
        addCharts();
    }

    private void addCharts() {
        metrics.addCustomChart(new NumberOfWorldsChart());
    }

}
