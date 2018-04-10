package com.roxiemobile.networkingapi.network.tls;

import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.logging.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

class TlsSocketFactoryProxy extends SSLSocketFactory
{
// MARK: - Construction

    TlsSocketFactoryProxy(SSLSocketFactory delegate) {
        mInternalSSLSocketFactory = delegate;
    }

// MARK: - Public functions

    @Override
    public String[] getDefaultCipherSuites() {
        return mInternalSSLSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return mInternalSSLSocketFactory.getSupportedCipherSuites();
    }
    
    @Override
    public Socket createSocket() throws IOException {
        return enableTlsOnSocket(mInternalSSLSocketFactory.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTlsOnSocket(mInternalSSLSocketFactory.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return enableTlsOnSocket(mInternalSSLSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return enableTlsOnSocket(mInternalSSLSocketFactory.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTlsOnSocket(mInternalSSLSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTlsOnSocket(mInternalSSLSocketFactory.createSocket(address, port, localAddress, localPort));
    }

// MARK: - Private functions

    private @Nullable Socket enableTlsOnSocket(@Nullable Socket socket) {

        if(socket instanceof SSLSocket) {
            SSLSocket sslSocket = (SSLSocket) socket;

            String[] supportedProtocols = sslSocket.getSupportedProtocols();

            List<String> protocolsToEnable = Stream.of(supportedProtocols)
                    // Only add TLS protocols (don't want to support older SSL versions)
                    .filter(protocol -> protocol.toUpperCase().contains("TLS"))
                    .collect(Collectors.toList());

            if (protocolsToEnable.isEmpty()) {
                Logger.e(TAG, "Socket does not support TLS, supported protocols: " + Arrays.toString(supportedProtocols));
            }

            // Enable protocols from our list - even if it's empty. No connection is better than
            // unsecured connection.
            String[] protocolArray = protocolsToEnable.toArray(new String[protocolsToEnable.size()]);
            sslSocket.setEnabledProtocols(protocolArray);
        }
        else {
            Logger.e(TAG, "Null or unsupported socket " + socket);
        }
        return socket;
    }

// MARK: - Constants

    private static final String TAG = TlsSocketFactoryProxy.class.getSimpleName();

// MARK: - Variables

    private final SSLSocketFactory mInternalSSLSocketFactory;
}