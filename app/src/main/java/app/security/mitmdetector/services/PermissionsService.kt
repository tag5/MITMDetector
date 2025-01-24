package app.security.mitmdetector.services

import javax.inject.Singleton
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Intent
import android.provider.Settings

@Singleton
class PermissionsService @Inject constructor(@ApplicationContext private val context: Context) {
    fun askForNotificationsPermissionIfNeeded(activity: Activity) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.areNotificationsEnabled()) {
            return
        }

        val builder = AlertDialog.Builder(activity)
            .setTitle("Notifications are disabled")
            .setMessage("You need to manually allow notifications.\n\nDo you want to open system settings to allow notifications for this app ?\n(Don't forget to enable 'Pop on screen' too)")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                activity.startActivity(intent)
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}