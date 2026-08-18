package com.Shoshin.app.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkStateMonitor(context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Seeded from the connection that already exists. Starting at `false` meant every consumer
     * rendered its offline state until the first onAvailable callback landed — on a screen that
     * swaps its whole body on !isOnline, that reads as the app randomly claiming to be offline.
     */
    private val _isOnline = MutableStateFlow(hasInternetNow())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    /**
     * Connectivity events are per-network, not global. A device holding Wi-Fi and cellular at
     * once gets onLost for the one it leaves during a handover, so treating any single loss as
     * "offline" drops the app into its offline state while it is still connected. Tracking the
     * live set means we only go offline once nothing is left.
     */
    private val availableNetworks = mutableSetOf<Network>()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            synchronized(availableNetworks) {
                availableNetworks.add(network)
                _isOnline.value = availableNetworks.isNotEmpty()
            }
        }

        override fun onLost(network: Network) {
            synchronized(availableNetworks) {
                availableNetworks.remove(network)
                // Fall back to a direct query rather than trusting the set alone: networks that
                // were already up before registration never produced an onAvailable for us.
                _isOnline.value = availableNetworks.isNotEmpty() || hasInternetNow()
            }
        }
    }

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    /** Callbacks are process-wide and survive the Activity; unregister or they accumulate. */
    fun unregister() {
        runCatching { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    private fun hasInternetNow(): Boolean {
        val active = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(active) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isCurrentlyOnline(): Boolean = _isOnline.value
}
