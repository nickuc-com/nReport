package com.nickuc.report.listener;

import com.nickuc.report.management.UserManagement;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityListener {

    private final UserManagement userManagement;

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        userManagement.invalidate(event.getPlayer().getUniqueId());
    }

}
