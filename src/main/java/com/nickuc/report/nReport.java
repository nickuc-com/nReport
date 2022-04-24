/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report;

import com.nickuc.report.bootstrap.Platform;
import com.nickuc.report.management.UserManagement;
import com.nickuc.report.model.Settings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class nReport {

    private final Platform<?> platform;
    private UserManagement userManagement;
    private Settings settings;

    public void enablePlugin() {

        String c = "§e";
        print(c + "         __                       _   ");
        print(c + " _ __   /__\\ ___ _ __   ___  _ __| |_ ");
        print(c + "| '_ \\ / \\/// _ \\ '_ \\ / _ \\| '__| __|");
        print(c + "| | | / _  \\  __/ |_) | (_) | |  | |_ ");
        print(c + "|_| |_\\/ \\_/\\___| .__/ \\___/|_|   \\__|");
        print(c + "                |_|                   ");
        print(c + "       github.com/nickuc/nReport");
        print("");

        userManagement = new UserManagement(platform);
        reloadConfig();
    }

    public void disablePlugin() {
    }

    public void print(String message) {
        platform.print(message);
    }

    public void reloadConfig() {
        settings = platform.loadSettings();
    }
}
