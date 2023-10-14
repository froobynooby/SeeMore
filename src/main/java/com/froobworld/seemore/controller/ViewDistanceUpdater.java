package com.froobworld.seemore.controller;

import com.destroystokyo.paper.event.player.PlayerClientOptionsChangeEvent;
import com.google.common.collect.Sets;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

public class ViewDistanceUpdater implements Listener {
    private final ViewDistanceController controller;
    private final Set<UUID> seenBefore = Sets.newConcurrentHashSet();

    public ViewDistanceUpdater(ViewDistanceController viewDistanceController) {
        this.controller = viewDistanceController;
    }

    @EventHandler
    private void onOptionsChange(PlayerClientOptionsChangeEvent event) {

        // the change check may fail if the player has just joined the server, so also check if we have seen them before
        boolean seen = seenBefore.contains(event.getPlayer().getUniqueId());

        if (event.hasViewDistanceChanged() || !seen) {
            seenBefore.add(event.getPlayer().getUniqueId());
            controller.setTargetViewDistance(event.getPlayer(), event.getViewDistance(), seen, !seen);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        seenBefore.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        controller.setTargetViewDistance(event.getPlayer(), event.getPlayer().getClientViewDistance(), false, false);
    }

}
