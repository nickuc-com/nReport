/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report.command.report;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nickuc.report.bootstrap.Platform;
import com.nickuc.report.model.Settings;
import com.nickuc.report.model.User;
import com.nickuc.report.nReport;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class ReportCommand<P> {

    private static final Cache<String, Long> DELAY_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    private final nReport plugin;
    private final Platform<P> platform;

    public void execute(P senderPlayer, String lb, String[] args) {
        if (args.length == 0) {
            platform.sendMessage(senderPlayer, "§cVocê deve usar: /" + lb.toLowerCase() + " <jogador>");
            return;
        }

        String targetName = args[0];
        if (targetName.equalsIgnoreCase("reload") && platform.hasPermission(senderPlayer, "nreport.admin")) {
            plugin.reloadConfig();
            platform.sendMessage(senderPlayer, "§aConfiguração e arquivos de linguagem foram recarregados.");
            return;
        }

        Settings settings = plugin.getSettings();
        String senderName = platform.getName(senderPlayer);

        Long delayTime = DELAY_CACHE.getIfPresent(senderName);
        long currentTime = System.currentTimeMillis();
        if (delayTime != null && currentTime - delayTime < settings.getDelayReports() * 1000L) {
            platform.sendMessage(senderPlayer, "§cVocê deve esperar para realizar outro report.");
            return;
        }

        P targetPlayer = platform.findPlayer(targetName);
        if (targetPlayer == null) {
            String finalTargetName = targetName;
            Optional<P> findPlayer = platform.getOnlinePlayers()
                    .filter(on -> platform.getName(on).toLowerCase(Locale.ENGLISH).startsWith(finalTargetName.toLowerCase(Locale.ENGLISH)))
                    .findFirst();

            if (!findPlayer.isPresent()) {
                platform.sendMessage(senderPlayer, "§cO jogador inserido está offline.");
                return;
            }

            targetPlayer = findPlayer.get();
            targetName = platform.getName(targetPlayer);
        }

        if (targetName.equals(senderName)) {
            platform.sendMessage(senderPlayer, "§cVocê não pode reportar a si mesmo.");
            return;
        }

        if (args.length == 1) {
            List<String> loadedReports = settings.getLoadedReports();
            if (!loadedReports.isEmpty()) {
                platform.sendMessage(senderPlayer, "");
                platform.sendMessage(senderPlayer, " §aPara concluir sua denúncia, escolha um dos motivos abaixo");
                platform.sendMessage(senderPlayer, "");
                boolean color = true;
                for (String report : settings.getLoadedReports()) {
                    color = !color;
                    platform.sendMessage(
                            senderPlayer,
                            "  §a▪ " + (color ? "§7" : "§f" + report),
                            "§7Clique para reportar este jogador por " + report + ".",
                            "/" + lb + " " + targetName + " " + report
                    );
                }
                if (settings.isAllowOtherReports()) {
                    platform. sendMessage(
                            senderPlayer,
                            "  §a▪ " + (color ? "§7" : "§f") + "Outro motivo",
                            "§7Clique para reportar este jogador por outro motivo.",
                            "/" + lb + " " + targetName + " "
                    );
                }
                platform.sendMessage(senderPlayer, "");
            } else {
                platform.sendMessage(senderPlayer, "§cUso: /" + lb + " <motivo>");
            }
            return;
        }

        String reportReason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (!settings.isAllowOtherReports() && settings.getLoadedReports().stream().noneMatch(reportReason::equalsIgnoreCase)) {
            platform.sendMessage(senderPlayer, "§cDesculpe, mas esse tipo de report não existe.");
            return;
        }

        DELAY_CACHE.put(senderName, currentTime);

        UUID targetUniqueId = platform.getUniqueId(targetPlayer);
        User user = plugin.getUserManagement().getOrLoadFromCache(targetUniqueId);
        user.addReport(senderName, reportReason);

        platform.sendMessage(senderPlayer, "§7Você reportou o jogador §f" + targetName + " §7por §f\"" + reportReason + "\".");
        plugin.print(senderName + " reportou " + targetName + " por \"" + reportReason + "\".");

        String finalTarget = targetName;
        P finalTargetPlayer = targetPlayer;
        platform.getOnlinePlayers().forEach(on -> {
            if (platform.hasPermission(on, "nreport.admin")) {
                platform.sendTitle(on, "§c§lReport", "§7Uma nova denúncia foi recebida", 0, 60, 10);
                platform.sendMessage(on, "");
                platform.sendMessage(on, " §cUma nova denúncia foi recebida.");
                platform.sendMessage(on, "");
                platform.sendMessage(on, " §8▪ §7Quem reportou: §f" + senderName);
                platform.sendMessage(on, " §8▪ §7Jogador reportado: §f" + finalTarget);
                platform.sendMessage(on, " §8▪ §7Motivo do report: §f'" + reportReason + "'");
                platform.sendServerConnectMessage(on, senderPlayer, finalTargetPlayer);
                platform.sendMessage(on, "");
            }
        });
    }
}
