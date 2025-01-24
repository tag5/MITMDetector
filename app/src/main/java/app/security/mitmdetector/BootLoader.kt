package app.security.mitmdetector

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import app.security.mitmdetector.data.AuditResult
import app.security.mitmdetector.services.DatabaseService
import app.security.mitmdetector.services.NotificationsService
import app.security.mitmdetector.services.vulnerabilitychecks.VulnerabilityChecksProvider
import javax.inject.Singleton

@Singleton
class NetworkCallback @Inject constructor(private val notificationsService: NotificationsService): ConnectivityManager.NetworkCallback() {
    @Inject
    lateinit var checks: VulnerabilityChecksProvider

    @Inject
    lateinit var db: DatabaseService

    override fun onAvailable(network: Network) {
        val results = checks.getAll()
            .filter { check -> db.isCheckEnabled(check.getCheckId()) }
            .map { check -> check.run() }
            .filterNot { it is AuditResult.NoAlert }

        if (results.size > 0) {
                val s = StringBuilder()
                for (result in results) {
                    if (result is AuditResult.VulnerabilityDetected) {
                        s.appendLine(result.message)
                    } else if (result is AuditResult.Error) {
                        s.appendLine("Network error: " + result.message)
                    }
                }

            notificationsService.sendNotification(s.toString())
        }
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