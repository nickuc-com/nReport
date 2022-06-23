/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report.bootstrap;

import com.nickuc.report.command.report.BukkitReportCommand;
import com.nickuc.report.command.reports.ReportesCommand;
import com.nickuc.report.inventory.InventoryListener;
import com.nickuc.report.listener.BukkitListener;
import com.nickuc.report.logging.LoggingProvider;
import com.nickuc.report.management.UserManagement;
import com.nickuc.report.model.Settings;
import com.nickuc.report.nReport;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class nReportBukkit extends JavaPlugin implements Platform<Player> {

    private nReport plugin;
    @Getter
    private LoggingProvider loggingProvider;

    @Override
    public void onEnable() {
        Logger javaLogger = getLogger();
        loggingProvider = new LoggingProvider() {
            @Override
            public void info(String message) {
                javaLogger.info(message);
            }

            @Override
            public void error(String message) {
                javaLogger.severe(message);
            }

            @Override
            public void warn(String message) {
                javaLogger.warning(message);
            }
        };

        plugin = new nReport(this);
        plugin.enablePlugin();

        Server server = getServer();

        // /report
        PluginCommand reportCommand = getCommand("report");
        if (reportCommand != null) {
            BukkitReportCommand executorAndTab = new BukkitReportCommand(plugin, this, server);
            reportCommand.setExecutor(executorAndTab);
            reportCommand.setTabCompleter(executorAndTab);
        }

        // /reports
        UserManagement userManagement = plugin.getUserManagement();
        PluginCommand reportsCommand = getCommand("reports");
        if (reportsCommand != null) {
            reportsCommand.setExecutor(new ReportesCommand(userManagement));
            reportsCommand.setPermission("nreport.admin");
            reportsCommand.setPermissionMessage("§cVocê não tem permissão para executar este comando.");
        }

        // register listener
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(new BukkitListener(userManagement), this);
        pluginManager.registerEvents(new InventoryListener(server, userManagement), this);

        new Metrics(this, 4076);
    }

    @Override
    public void onDisable() {
        if (plugin != null) {
            plugin.disablePlugin();
            plugin = null;
        }
    }

    @Override
    public void print(String message) {
        getServer().getConsoleSender().sendMessage(message);
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public Settings loadSettings() {
        saveDefaultConfig();
        reloadConfig();

        FileConfiguration config = getConfig();
        List<String> loadedReports = config.getStringList("reports");
        int delayReports = config.getInt("delay", 5);
        boolean allowOtherReason = config.getBoolean("allow-other-reason");

        return new Settings(loadedReports, delayReports, allowOtherReason);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<Player> getOnlinePlayers() {
        return (Stream<Player>) getServer().getOnlinePlayers().stream();
    }

    @Nullable
    @Override
    public Player findPlayer(String playerName) {
        return getServer().getPlayerExact(playerName);
    }

    @Override
    public void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(Player player, String message, @Nullable String hover, @Nullable String suggest) {
        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(message));
        if (hover != null) {
            textComponent.setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
        }
        if (suggest != null) {
            textComponent.setClickEvent(
                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        }
        player.spigot().sendMessage(textComponent);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } catch (NoSuchMethodError ignored) {
            // hard-coded legacy version support
        }
    }

    @Override
    public String getName(Player player) {
        return player.getName();
    }

    @Override
    public UUID getUniqueId(Player player) {
        return player.getUniqueId();
    }

    @Override
    public boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }
}
