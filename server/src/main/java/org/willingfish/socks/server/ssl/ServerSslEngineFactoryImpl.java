package org.willingfish.socks.server.ssl;

import lombok.extern.slf4j.Slf4j;
import org.willingfish.socks.common.ssl.ISslEngineFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Slf4j
public class ServerSslEngineFactoryImpl implements ISslEngineFactory {

    SSLContext context ;
    public ServerSslEngineFactoryImpl(String caPath, String pass) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        KeyManagerFactory kmf = null;
        try(InputStream in= new FileInputStream(caPath)) {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(in, pass.toCharArray());
            kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, pass.toCharArray());
        }
        TrustManagerFactory tf = null;
        try(InputStream in= new FileInputStream(caPath)) {
            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(in, pass.toCharArray());
            tf = TrustManagerFactory.getInstance("SunX509");
            tf.init(tks);
        }
        context = SSLContext.getInstance(PROTOCOL);
        context.init(kmf.getKeyManagers(),tf.getTrustManagers(), null);
    }

    @Override
    public SSLEngine createSslEngine() {
        return context.createSSLEngine();
    }
}
