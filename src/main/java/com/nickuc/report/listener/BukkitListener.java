package com.nickuc.report.listener;

import com.nickuc.report.management.UserManagement;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class BukkitListener implements Listener {

    private final UserManagement userManagement;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        userManagement.invalidate(event.getPlayer().getUniqueId());
    }

}
