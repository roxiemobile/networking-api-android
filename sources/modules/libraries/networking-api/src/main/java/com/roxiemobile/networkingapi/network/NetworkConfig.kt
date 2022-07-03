package com.roxiemobile.networkingapi.network

import java.util.concurrent.TimeUnit

object NetworkConfig {

// MARK: - Constants

    object Timeout {
        val CONNECTION = TimeUnit.SECONDS.toMillis(60)
        val READ = TimeUnit.SECONDS.toMillis(30)
    }
}
