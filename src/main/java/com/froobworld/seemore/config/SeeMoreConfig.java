package com.froobworld.seemore.config;

import com.froobworld.nabconfiguration.*;
import com.froobworld.nabconfiguration.annotations.Entry;
import com.froobworld.nabconfiguration.annotations.SectionMap;
import com.froobworld.seemore.SeeMore;
import org.bukkit.World;

import java.io.File;

public class SeeMoreConfig extends NabConfiguration {
    private static final int VERSION = 1;

    public SeeMoreConfig(SeeMore seeMore) {
        super(
                new File(seeMore.getDataFolder(), "config.yml"),
                () -> seeMore.getResource("config.yml"),
                i -> seeMore.getResource("config-patches/" + i + ".patch"),
                VERSION
        );
    }

    @Entry(key = "update-delay")
    public final ConfigEntry<Integer> updateDelay = ConfigEntries.integerEntry();

    @SectionMap(key = "world-settings", defaultKey = "default")
    public final ConfigSectionMap<World, WorldSettings> worldSettings = new ConfigSectionMap<>(World::getName, WorldSettings.class, true);

    public static class WorldSettings extends ConfigSection {

        @Entry(key = "maximum-view-distance")
        public final ConfigEntry<Integer> maximumViewDistance = ConfigEntries.integerEntry();

    }

}
