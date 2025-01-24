package app.security.mitmdetector

import javax.inject.Singleton
import javax.inject.Inject
import android.net.ConnectivityManager
import android.net.Network
import app.security.mitmdetector.data.AuditResult
import app.security.mitmdetector.services.DatabaseService
import app.security.mitmdetector.services.NotificationsService
import app.security.mitmdetector.services.vulnerabilitychecks.VulnerabilityChecksProvider
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * This NetworkCallback class is registred from BootLoader
 */
@Singleton
class NetworkCallback @Inject constructor(): ConnectivityManager.NetworkCallback() {
    @Inject
    lateinit var notificationsService: NotificationsService

    @Inject
    lateinit var checks: VulnerabilityChecksProvider

    @Inject
    lateinit var db: DatabaseService

    private fun launchChecks() {
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

    override fun onAvailable(network: Network) {
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            MainScope().launch(Dispatchers.IO) {
                launchChecks()
            }
        }, 20000)
    }

    override fun onLost(network: Network) {

    }
}
