/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report.inventory;

import com.nickuc.report.inventory.holder.MainMenuHolder;
import com.nickuc.report.inventory.holder.PlayerReportsHolder;
import com.nickuc.report.inventory.holder.nReportHolder;
import com.nickuc.report.management.UserManagement;
import com.nickuc.report.model.User;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class InventoryListener implements Listener {

    private final Server server;
    private final UserManagement userManagement;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof nReportHolder) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                Player player = (Player) event.getWhoClicked();
                if (holder instanceof PlayerReportsHolder) {
                    ClickType clickType = event.getClick();
                    switch (clickType) {
                        case RIGHT:
                        case SHIFT_RIGHT:
                            userManagement.delete(((PlayerReportsHolder) holder).getUniqueId());

                        case LEFT:
                        case SHIFT_LEFT:
                            player.closeInventory();
                            player.chat("/reportes");
                            break;
                    }
                } else if (holder instanceof MainMenuHolder) {
                    String playerName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    Player targetPlayer = server.getPlayer(playerName);
                    if (targetPlayer == null) {
                        player.closeInventory();
                        player.chat("/reportes");
                        return;
                    }

                    User user = userManagement.getOrLoadFromCache(targetPlayer.getUniqueId());

                    PlayerReportsHolder playerReportsHolder = new PlayerReportsHolder(targetPlayer.getUniqueId());
                    Inventory newInventory = server.createInventory(playerReportsHolder, 3 * 9, "§8Reports de " + playerName);
                    playerReportsHolder.init(newInventory);

                    newInventory.setItem(13, createItemStack(user));
                    player.openInventory(newInventory);
                } else {
                    throw new IllegalArgumentException("Holder " + holder.getClass().getCanonicalName() + " not implemented yet!");
                }
            }
        }
    }

    private ItemStack createItemStack(User user) {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalArgumentException("ItemMeta cannot be null!");
        }
        itemMeta.setDisplayName("§aInformações das denúncias");
        itemStack.setDurability((short) 5);

        List<String> lore = new ArrayList<>();
        lore.add("§7Total de denúncias: " + user.getReportList().size());
        lore.add("");
        lore.add("§7Denúncias mais recebidas:");
        user.getOrderedReports(3).forEach(entry ->
                lore.add(String.format("§7- %s: §f%d", entry.getKey(), entry.getValue())));
        lore.add("");
        lore.add("§7Clique com o botão §8direito §7para apagar este report.");
        lore.add("§7Clique com o botão §8esquerdo §7para voltar para o menu de reportes.");
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
