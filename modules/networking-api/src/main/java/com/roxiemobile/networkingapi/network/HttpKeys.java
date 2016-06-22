package com.roxiemobile.networkingapi.network;

public interface HttpKeys
{
// MARK: - Constants

    interface MethodName {
        // Values
        String GET = "GET";
        String POST = "POST";
        String PUT = "PUT";
        String PATCH = "PATCH";
        String DELETE = "DELETE";
        String HEAD = "HEAD";
        String OPTIONS = "OPTIONS";
        String TRACE = "TRACE";
    }

    interface CharsetName {
        // Values
        String ISO_8859_1 = "ISO-8859-1";
        String US_ASCII = "US-ASCII";
        String UTF_8 = "UTF-8";
        String UTF_16 = "UTF-16";
        String UTF_16BE = "UTF-16BE";
        String UTF_16LE = "UTF-16LE";
    }

}
