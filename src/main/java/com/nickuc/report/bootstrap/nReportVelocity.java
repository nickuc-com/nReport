/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report.bootstrap;

import com.google.inject.Inject;
import com.nickuc.report.command.report.VelocityReportCommand;
import com.nickuc.report.listener.VelocityListener;
import com.nickuc.report.model.Settings;
import com.nickuc.report.nReport;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Plugin(
        id = "nreport",
        name = "nReport",
        version = "@version",
        description = "Um plugin simples de report.",
        url = "https://www.nickuc.com",
        authors = "NickUC"
)
public class nReportVelocity implements Platform<Player> {

    private nReport plugin;

    private final ProxyServer server;
    private final Logger logger;
    @Getter
    private final File dataFolder;

    @Inject
    public nReportVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataDirectory.toFile();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        plugin = new nReport(this);
        plugin.enablePlugin();

        // /report
        CommandManager commandManager = server.getCommandManager();
        CommandMeta reportCommandMeta = commandManager.metaBuilder("report")
                .aliases("reportar")
                .build();
        commandManager.register(reportCommandMeta, new VelocityReportCommand(plugin, this, server));

        // register listeners
        server.getEventManager().register(this, new VelocityListener(plugin.getUserManagement()));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (plugin != null) {
            plugin.disablePlugin();
            plugin = null;
        }
    }

    @Override
    public void print(String message) {
        logger.info(message);
    }

    @Override
    public Settings loadSettings() {
        return new Settings(Collections.emptyList(), 30, true); // todo: missing support
    }

    @Override
    public Stream<Player> getOnlinePlayers() {
        return server.getAllPlayers().stream();
    }

    @Nullable
    @Override
    public Player findPlayer(String playerName) {
        return server.getPlayer(playerName).orElse(null);
    }

    @Override
    public void sendMessage(Player player, String message) {
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }

    @Override
    public void sendMessage(Player player, String message, @Nullable String hover, @Nullable String suggest) {
        TextComponent textComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        if (hover != null) {
            textComponent = textComponent.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(hover)));
        }
        if (suggest != null) {
            textComponent = textComponent.clickEvent(ClickEvent.suggestCommand(suggest));
        }
        player.sendMessage(textComponent);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(Title.title(
                LegacyComponentSerializer.legacyAmpersand().deserialize(title),
                LegacyComponentSerializer.legacyAmpersand().deserialize(subtitle),
                Title.Times.of(
                        Duration.ofSeconds(fadeIn * 50L),
                        Duration.ofSeconds(stay * 50L),
                        Duration.ofSeconds(fadeOut * 50L)
                )
        ));
    }

    @Override
    public String getName(Player player) {
        return player.getUsername();
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
