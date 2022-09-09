package com.nickuc.report.listener;

import com.nickuc.report.management.UserManagement;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class BungeeListener implements Listener {

    private final UserManagement userManagement;

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        userManagement.invalidate(event.getPlayer().getUniqueId());
    }

}
