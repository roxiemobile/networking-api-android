package com.roxiemobile.networkingapi.network.security;

import org.jetbrains.annotations.NotNull;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public final class TLSCompat
{
// MARK: - Construction

    private TLSCompat() {
        // Do nothing
    }

// MARK: - Methods

    public static void enableTlsOnSockets(@NotNull OkHttpClient.Builder builder) {
        try {
            // Based on code from OkHttpClient.Builder.sslSocketFactory javadoc

            X509TrustManager trustManager = getDefaultTrustManager();
            SSLSocketFactory sslSocketFactory = getDefaultSocketFactory(trustManager);
            TLSSocketFactoryProxy socketFactoryProxy = new TLSSocketFactoryProxy(sslSocketFactory);

            builder.sslSocketFactory(socketFactoryProxy, trustManager);
        }
        catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new IllegalStateException("Failed to init compat SSL factory.", e);
        }
    }

// MARK: - Private Methods

    private static @NotNull X509TrustManager getDefaultTrustManager()
            throws NoSuchAlgorithmException, KeyStoreException {

        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
        trustManagerFactory.init((KeyStore) null);

        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager)) {
            return (X509TrustManager) trustManagers[0];
        }
        else {
            throw new IllegalStateException("Unexpected default trust managers: " + Arrays.toString(trustManagers));
        }
    }

    private static @NotNull SSLSocketFactory getDefaultSocketFactory(X509TrustManager trustManager)
            throws KeyManagementException, NoSuchAlgorithmException {

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, null);

        return sslContext.getSocketFactory();
    }
}
