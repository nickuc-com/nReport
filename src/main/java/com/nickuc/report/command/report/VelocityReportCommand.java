package com.nickuc.report.command.report;

import com.nickuc.report.bootstrap.Platform;
import com.nickuc.report.nReport;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class VelocityReportCommand extends ReportCommand<Player> implements SimpleCommand {

    private final ProxyServer server;

    public VelocityReportCommand(nReport plugin, Platform<Player> platform, ProxyServer server) {
        super(plugin, platform);
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text("§cEste comando não está disponível para o console."));
            return;
        }

        execute((Player) source, invocation.alias(), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] arguments = invocation.arguments();
        if (arguments.length > 0) {
            String lastArgument = arguments[arguments.length - 1];
            if (lastArgument.length() > 1) {
                String lastArgumentLower = lastArgument.toLowerCase(Locale.ENGLISH);
                return server.getAllPlayers()
                        .stream()
                        .map(Player::getUsername)
                        .filter(username -> username.toLowerCase(Locale.ENGLISH).startsWith(lastArgumentLower))
                        .collect(Collectors.toList());
            }
        }
        return SimpleCommand.super.suggest(invocation);
    }
}
