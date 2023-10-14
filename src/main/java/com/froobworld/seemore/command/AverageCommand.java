package com.froobworld.seemore.command;

import com.froobworld.seemore.SeeMore;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static net.kyori.adventure.text.Component.*;

public class AverageCommand implements CommandExecutor, TabCompleter {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.0");
    private final SeeMore seeMore;

    public AverageCommand(SeeMore seeMore) {
        this.seeMore = seeMore;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        Map<World, AtomicInteger> chunkCountMap = new ConcurrentHashMap<>();
        Map<World, AtomicInteger> playerCountMap = new ConcurrentHashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            CompletableFuture<Void> playerFuture = new CompletableFuture<>();
            future = future.thenCompose(v -> playerFuture);
            seeMore.getSchedulerHook().runEntityTaskAsap(() -> {
                chunkCountMap.compute(player.getWorld(), (world, chunkCount) -> {
                    if (chunkCount == null) {
                        chunkCount = new AtomicInteger();
                    }
                    try {
                        int viewDistance = player.getViewDistance();
                        chunkCount.addAndGet((2 * viewDistance + 1) * (2 * viewDistance + 1));
                        playerCountMap.computeIfAbsent(world, w -> new AtomicInteger()).getAndIncrement();
                    } catch (Throwable ignored) {}
                    return chunkCount;
                });
                playerFuture.complete(null);
            }, () -> playerFuture.complete(null), player);
        }

        future.thenRun(() -> {
            Map<World, Double> effectiveViewDistanceMap = new HashMap<>();
            int totalChunkCount = 0;
            int totalPlayerCount = 0;
            for (World world : chunkCountMap.keySet()) {
                int chunkCount = chunkCountMap.get(world).get();
                int playerCount = playerCountMap.get(world).get();
                if (playerCount == 0) {
                    continue;
                }

                double effectiveViewDistance = (Math.sqrt((double) chunkCount / (double) playerCount) - 1.0) / 2.0;
                effectiveViewDistanceMap.put(world, effectiveViewDistance);

                totalChunkCount += chunkCount;
                totalPlayerCount += playerCount;
            }
            double totalEffectiveViewDistance = totalPlayerCount == 0 ? 0 : (Math.sqrt((double) totalChunkCount / (double) totalPlayerCount) - 1.0) / 2.0;

            if (totalPlayerCount == 0) {
                sender.sendMessage(text("There are no players online.", NamedTextColor.GRAY));
                return;
            }

            sender.sendMessage(text("Effective average view distance:", NamedTextColor.GRAY));
            sender.sendMessage(
                    text("All worlds: ", NamedTextColor.GOLD)
                            .append(text(formatViewDistance(totalEffectiveViewDistance), NamedTextColor.RED))
            );
            sender.sendMessage(empty());
            sender.sendMessage(text("--------------------------"));
            for (World world : Bukkit.getWorlds()) {
                double effectiveViewDistance = effectiveViewDistanceMap.getOrDefault(world, 0.0);
                sender.sendMessage(
                        text(world.getName() + ": ", NamedTextColor.GOLD)
                                .append(text(formatViewDistance(effectiveViewDistance), NamedTextColor.RED))
                );
            }
            sender.sendMessage(text("--------------------------"));
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private static String formatViewDistance(double viewDistance) {
        if (viewDistance == 0) {
            return "-";
        }
        return DECIMAL_FORMAT.format(viewDistance);
    }

}
