/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report.inventory;

import com.nickuc.report.management.UserManagement;
import com.nickuc.report.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@AllArgsConstructor
public class InventoryListener implements Listener {

    private final Server server;
    private final UserManagement userManagement;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            Player player = (Player) event.getWhoClicked();
            String invName = event.getView().getTitle();
            if (invName.equalsIgnoreCase("§8Todos os Reports")) {
                event.setCancelled(true);

                String playerName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                Player targetPlayer = server.getPlayer(playerName);
                if (targetPlayer == null) {
                    player.closeInventory();
                    player.chat("/reportes");
                    return;
                }

                User user = userManagement.getOrLoadFromCache(targetPlayer.getUniqueId());

                Holder holder = new Holder(targetPlayer.getUniqueId());
                Inventory newInventory = server.createInventory(null, 3 * 9, "§8Reports de " + playerName);
                holder.inventory = newInventory;

                newInventory.setItem(13, createItemStack(user));
                player.openInventory(newInventory);
            } else {
                InventoryHolder inventoryHolder = inventory.getHolder();
                if (inventoryHolder instanceof Holder) {
                    event.setCancelled(true);

                    ClickType clickType = event.getClick();
                    switch (clickType) {
                        case RIGHT:
                        case SHIFT_RIGHT:
                            userManagement.delete(((Holder) inventoryHolder).uniqueId);

                        case LEFT:
                        case SHIFT_LEFT:
                            player.closeInventory();
                            player.chat("/reportes");
                            break;
                    }
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

    @RequiredArgsConstructor
    public static class Holder implements InventoryHolder {

        private final UUID uniqueId;
        @Getter
        private Inventory inventory;

    }

}
