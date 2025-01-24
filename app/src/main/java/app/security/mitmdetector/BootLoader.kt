package app.security.mitmdetector

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.security.mitmdetector.services.NotificationsService

@AndroidEntryPoint
class BootLoader : BroadcastReceiver() {
    @Inject
    lateinit var notificationsService: NotificationsService

    @Override
    override fun onReceive(context: Context, intent: Intent) {
        notificationsService.sendNotification("[tmp] BootLoader.onReceive test !")
    }
}