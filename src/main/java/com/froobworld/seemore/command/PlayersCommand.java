package com.froobworld.seemore.command;

import com.froobworld.seemore.SeeMore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.*;

public class PlayersCommand implements CommandExecutor, TabCompleter {
    private final SeeMore seeMore;

    public PlayersCommand(SeeMore seeMore) {
        this.seeMore = seeMore;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        Map<Integer, List<String>> playerViewDistanceMap = new ConcurrentHashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            CompletableFuture<Void> playerFuture = new CompletableFuture<>();
            future = future.thenCompose(v -> playerFuture);
            seeMore.getSchedulerHook().runEntityTaskAsap(() -> {
                try {
                    int viewDistance = player.getViewDistance();
                    playerViewDistanceMap.compute(viewDistance, (key, playerList) -> {
                        if (playerList == null) {
                            playerList = new ArrayList<>();
                        }
                        playerList.add(player.getName());
                        return playerList;
                    });
                } catch (Throwable ignored) {}
                playerFuture.complete(null);
            }, () -> playerFuture.complete(null), player);
        }

        future.thenRun(() -> {
            TreeMap<Integer, List<String>> finalPlayerViewDistanceMap = new TreeMap<>(Collections.reverseOrder());
            finalPlayerViewDistanceMap.putAll(playerViewDistanceMap);

            if (finalPlayerViewDistanceMap.isEmpty()) {
                sender.sendMessage(text("There are no players online.", NamedTextColor.GRAY));
                return;
            }

            sender.sendMessage(text("Players by view distance:", NamedTextColor.GRAY));
            for (Map.Entry<Integer, List<String>> entry : finalPlayerViewDistanceMap.entrySet()) {
                List<Component> playerNames = entry.getValue().stream()
                        .sorted(String::compareToIgnoreCase)
                        .map(name -> text(name, NamedTextColor.WHITE))
                        .collect(Collectors.toList());
                sender.sendMessage(
                        text(entry.getKey() + ": ", NamedTextColor.GOLD)
                                .append(Component.join(JoinConfiguration.separator(text(" | ", NamedTextColor.RED)), playerNames))
                );
            }
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }

}
