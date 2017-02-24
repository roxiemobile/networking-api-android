package com.roxiemobile.networkingapi.network;

import com.roxiemobile.networkingapi.network.HttpKeys.CharsetName;

import java.nio.charset.Charset;

public interface NetworkConfig
{
// MARK: - Constants

    interface Timeout
    {
        // In milliseconds
        int CONNECTION = 60 * 1000;
        int READ = 30 * 1000;
    }

    // FIXME: @see HttpKeys.EncodingNames.UTF_8
    @Deprecated
    interface DefaultCharset
    {
        // Values
        Charset UTF_8 = Charset.forName(CharsetName.UTF_8);
        Charset ISO_8859_1 = Charset.forName(CharsetName.ISO_8859_1);
    }
}
