package app.security.mitmdetector

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

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