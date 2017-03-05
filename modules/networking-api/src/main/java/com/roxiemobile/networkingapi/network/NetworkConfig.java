package com.roxiemobile.networkingapi.network;

public interface NetworkConfig
{
// MARK: - Constants

    interface Timeout
    {
        // In milliseconds
        int CONNECTION = 60 * 1000;
        int READ = 30 * 1000;
    }
}
