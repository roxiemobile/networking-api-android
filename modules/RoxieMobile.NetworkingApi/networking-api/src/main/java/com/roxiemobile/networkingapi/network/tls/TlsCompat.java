package com.roxiemobile.networkingapi.network.tls;

import android.support.annotation.NonNull;

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

public final class TlsCompat
{
// MARK: - Construction

    private TlsCompat() {
    }

// MARK: - Public static functions

    // Based on code from OkHttpClient.Builder.sslSocketFactory javadoc
    public static void enableTlsOnSockets(@NonNull OkHttpClient.Builder builder) {
        try {
            X509TrustManager trustManager = getDefaultTrustManager();
            SSLSocketFactory sslSocketFactory = getDefaultSocketFactory(trustManager);

            TlsSocketFactoryProxy socketFactoryProxy = new TlsSocketFactoryProxy(sslSocketFactory);

            builder.sslSocketFactory(socketFactoryProxy, trustManager);
        }
        catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new IllegalStateException("Failed to init compat ssl factory", e);
        }
    }

// MARK: - Private static functions

    private static @NonNull X509TrustManager getDefaultTrustManager()
            throws NoSuchAlgorithmException, KeyStoreException {

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());

        trustManagerFactory.init((KeyStore) null);

        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }

        return (X509TrustManager) trustManagers[0];
    }

    private static @NonNull SSLSocketFactory getDefaultSocketFactory(X509TrustManager trustManager)
            throws KeyManagementException, NoSuchAlgorithmException {

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { trustManager }, null);
        return sslContext.getSocketFactory();
    }
}
