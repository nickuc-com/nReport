package com.nickuc.report.command.report;

import com.nickuc.report.bootstrap.Platform;
import com.nickuc.report.nReport;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BungeeReportCommand extends ReportCommand<ProxiedPlayer> {

    public BungeeReportCommand(nReport plugin, Platform<ProxiedPlayer> platform) {
        super(plugin, platform);
    }

    public Command createCommand(String label, String... aliases) {
        return new Command(label, null, aliases) {
            @Override
            public void execute(CommandSender sender, String[] args) {
                if (!(sender instanceof ProxiedPlayer)) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cEste comando não está disponível para o console."));
                    return;
                }

                BungeeReportCommand.this.execute((ProxiedPlayer) sender, label, args);
            }
        };
    }
}
