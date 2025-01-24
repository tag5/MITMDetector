package app.security.mitmdetector

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import app.security.mitmdetector.services.NotificationsService
import app.security.mitmdetector.services.vulnerabilitychecks.DNSVulnerabilityCheck
import javax.inject.Singleton

@Singleton
class NetworkCallback @Inject constructor(private val notificationsService: NotificationsService): ConnectivityManager.NetworkCallback() {
    @Inject
    lateinit var dnsCheck: DNSVulnerabilityCheck

    override fun onAvailable(network: Network) {
        dnsCheck.run()
    }

    override fun onLost(network: Network) {

    }
}

@AndroidEntryPoint
class BootLoader : BroadcastReceiver() {
    companion object {
        var isCallbackRegistered = false
    }

    @Inject
    lateinit var networkCallback: NetworkCallback

    @Override
    override fun onReceive(context: Context, intent: Intent) {
        if (!isCallbackRegistered) {
            isCallbackRegistered = true

            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        }
    }
}