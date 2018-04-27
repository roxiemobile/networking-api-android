package com.roxiemobile.networkingapi.network.security;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.logging.Logger;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

// NOTE: package private
final class TLSSocketFactoryProxy extends SSLSocketFactory
{
// MARK: - Construction

    // NOTE: package private
    TLSSocketFactoryProxy(SSLSocketFactory sslSocketFactory) {
        // Init instance variables
        mSSLSocketFactory = sslSocketFactory;
    }

// MARK: - Methods

    @Override
    public String[] getDefaultCipherSuites() {
        return mSSLSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return mSSLSocketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return enableTlsOnSocket(mSSLSocketFactory.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTlsOnSocket(mSSLSocketFactory.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return enableTlsOnSocket(mSSLSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return enableTlsOnSocket(mSSLSocketFactory.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTlsOnSocket(mSSLSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTlsOnSocket(mSSLSocketFactory.createSocket(address, port, localAddress, localPort));
    }

// MARK: - Private Methods

    private @Nullable Socket enableTlsOnSocket(@Nullable Socket socket) {

        if (socket == null) {
            Logger.w(TAG, "Passed socket is NULL.");
        }
        else if (socket instanceof SSLSocket) {
            SSLSocket sslSocket = (SSLSocket) socket;
            String[] supportedProtocols = sslSocket.getSupportedProtocols();

            List<String> tlsProtocols = Stream.of(supportedProtocols)
                    // Only add TLS protocols (don't want to support older SSL versions)
                    .filter(protocol -> protocol.toUpperCase().contains("TLS"))
                    .collect(Collectors.toList());

            if (tlsProtocols.isEmpty()) {
                Logger.e(TAG, "Socket does not support TLS, supported protocols: " +
                        Arrays.toString(supportedProtocols));
            }

            // Enable protocols from our list - even if it's empty. No connection is better than unsecured connection.
            String[] protocolsToEnable = tlsProtocols.toArray(new String[tlsProtocols.size()]);
            sslSocket.setEnabledProtocols(protocolsToEnable);
        }
        else {
            Logger.w(TAG, "Unsupported type of a socket: " + socket);
        }

        return socket;
    }

// MARK: - Constants

    private static final String TAG = TLSSocketFactoryProxy.class.getSimpleName();

// MARK: - Variables

    private final SSLSocketFactory mSSLSocketFactory;
}