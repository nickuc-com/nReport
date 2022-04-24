/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report.command.reports;

import com.nickuc.report.inventory.SkullItem;
import com.nickuc.report.management.UserManagement;
import com.nickuc.report.model.Report;
import com.nickuc.report.model.User;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@RequiredArgsConstructor
public class ReportesCommand implements CommandExecutor {

    private static final String DEFAULT_SKULL_URL = "http://textures.minecraft.net/texture/252568bcf6958fbd7857feaf15faee5c9fc39cf12c114db50a430b18df138e10";

    private final UserManagement userManagement;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando não está disponível para o console.");
            return true;
        }

        Player player = (Player) sender;
        Inventory newInventory = Bukkit.createInventory(null, 6 * 9, "§8Todos os Reports");

        int itemIndex = 10;
        players_loop: for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            User user = userManagement.getOrLoadFromCache(onlinePlayer.getUniqueId());
            Report lastReport = user.lastReport();
            if (lastReport == null) {
                continue;
            }

            ItemStack itemStack = SkullItem.createSkullItem(DEFAULT_SKULL_URL);

            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setDisplayName("§b" + onlinePlayer.getName());
            skullMeta.setLore(Arrays.asList(
                    "§7Total de denúncias: §f" + user.getReportList().size(),
                    "",
                    "§7Clique aqui para ver mais detalhes."
            ));
            skullMeta.setOwner(onlinePlayer.getName());

            itemStack.setItemMeta(skullMeta);
            newInventory.setItem(itemIndex, itemStack);

            switch (itemIndex) {
                case 16:
                    itemIndex = 19;
                    break;
                case 25:
                    itemIndex = 28;
                    break;
                case 34:
                    itemIndex = 37;
                    break;
                case 43:
                    sender.sendMessage("§cExistem mais denúncias, mas nem todas puderam ser carregadas por falta de espaço.");
                    break players_loop;
            }

            itemIndex++;
        }
        player.openInventory(newInventory);
        return false;
    }
}
