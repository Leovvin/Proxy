package org.willingfish.socks.common.ssl;

import javax.net.ssl.SSLEngine;

public interface ISslEngineFactory {
    String PROTOCOL = "TLS";

    SSLEngine createSslEngine();
}
