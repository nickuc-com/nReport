package com.nickuc.report.inventory.holder;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerReportsHolder extends nReportHolder {

    private final UUID uniqueId;

    public PlayerReportsHolder(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
