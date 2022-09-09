package com.nickuc.report.command.report;

import com.nickuc.report.bootstrap.Platform;
import com.nickuc.report.nReport;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BukkitReportCommand extends ReportCommand<Player> implements CommandExecutor, TabCompleter {

    private final Server server;

    public BukkitReportCommand(nReport plugin, Platform<Player> platform, Server server) {
        super(plugin, platform);
        this.server = server;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando não está disponível para o console.");
            return true;
        }

        execute((Player) sender, label, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length > 0) {
            String lastArgument = args[args.length - 1];
            if (lastArgument.length() > 1) {
                String lastArgumentLower = lastArgument.toLowerCase(Locale.ENGLISH);
                return server.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .filter(username -> username.toLowerCase(Locale.ENGLISH).startsWith(lastArgumentLower))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }
}
