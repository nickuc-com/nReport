package com.nickuc.report.bootstrap;

public interface ProxyPlatform<P> extends Platform<P> {

    void sendServerConnectMessage(P adminPlayer, P sender, P target);

}
