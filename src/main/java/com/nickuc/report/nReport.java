/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report;

import com.nickuc.report.bootstrap.Platform;
import com.nickuc.report.http.HttpClient;
import com.nickuc.report.management.UserManagement;
import com.nickuc.report.model.Settings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

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

        detectUpdates();
    }

    public void disablePlugin() {
    }

    public void print(String message) {
        platform.print(message);
    }

    public void reloadConfig() {
        settings = platform.loadSettings();
    }

    private void detectUpdates() {
        String latestVersion = null;
        String tagName = null;
        try {
            String result = HttpClient.DEFAULT.get("https://api.github.com/repos/nickuc/nReport/releases/latest");

            // avoid use Google Gson (no such library for legacy versions).
            if (result.contains("\"tag_name\":\"")) {
                tagName = result.split("\"tag_name\":\"")[1];
                if (tagName.contains("\",")) {
                    tagName = latestVersion = tagName.split("\",")[0];
                }
            }

            if (tagName == null) {
                platform.getLoggingProvider().error("Failed to find new updates: invalid response.");
            } else {
                String currentVersion = "v" + platform.getVersion();
                if (!currentVersion.equals(tagName)) {
                    platform.getLoggingProvider().warn("A new version of nReport is available (" + currentVersion + " -> " + latestVersion + ").");
                    platform.getLoggingProvider().info("Download: https://github.com/nickuc/nReport/releases");
                }
            }
        } catch (IOException e) {
            platform.getLoggingProvider().error("Failed to find new updates.");
        }

    }
}
