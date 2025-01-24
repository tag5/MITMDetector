package app.security.mitmdetector

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.security.mitmdetector.services.PermissionsService
import app.security.mitmdetector.ui.theme.MITMDetectorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var perms: PermissionsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MITMDetectorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }

        perms.askForNotificationsPermissionIfNeeded(this)

        val intent = Intent("app.security.mitmdetector.ACTION_APP_STARTED")
        sendBroadcast(intent)
    }
}

@Composable
fun MainScreen() {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Text(
            text = "MITM Detector",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

