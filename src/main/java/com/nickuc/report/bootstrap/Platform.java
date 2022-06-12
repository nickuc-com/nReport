package com.nickuc.report.bootstrap;

import com.nickuc.report.logging.LoggingProvider;
import com.nickuc.report.model.Settings;

import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;
import java.util.stream.Stream;

public interface Platform<P> {

    void print(String message);

    LoggingProvider getLoggingProvider();

    String getVersion();

    File getDataFolder();

    Settings loadSettings();

    Stream<P> getOnlinePlayers();

    @Nullable
    P findPlayer(String playerName);

    void sendMessage(P player, String message);

    void sendMessage(P player, String message, @Nullable String hover, @Nullable String suggest);

    void sendTitle(P player, String title, String subtitle, int fadeIn, int stay, int fadeOut);

    default void sendServerConnectMessage(P adminPlayer, P sender, P target) {
    }

    String getName(P player);

    UUID getUniqueId(P player);

    boolean hasPermission(P player, String permission);

}
