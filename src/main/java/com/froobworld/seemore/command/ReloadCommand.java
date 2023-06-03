package com.froobworld.seemore.command;

import com.froobworld.seemore.SeeMore;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.*;

public class ReloadCommand implements CommandExecutor, TabCompleter {
    private final SeeMore seeMore;

    public ReloadCommand(SeeMore seeMore) {
        this.seeMore = seeMore;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            seeMore.reload();
            sender.sendMessage(text("Plugin reloaded.", NamedTextColor.GRAY));
        } catch (Exception e) {
            sender.sendMessage(text("There was an error while reloading the plugin. See console for details.", NamedTextColor.RED));
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
