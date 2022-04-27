package com.roxiemobile.networkingapi.network;

public interface NetworkConfig
{
// MARK: - Constants

    interface Timeout
    {
        // In milliseconds
        long CONNECTION = 60 * 1000;
        long READ = 30 * 1000;
    }
}
