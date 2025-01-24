package app.security.mitmdetector

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import app.security.mitmdetector.services.DatabaseService
import app.security.mitmdetector.services.PermissionsService
import app.security.mitmdetector.services.vulnerabilitychecks.VulnerabilityChecksProvider
import app.security.mitmdetector.ui.theme.MITMDetectorTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChecksViewModel @Inject constructor(
    checksService: VulnerabilityChecksProvider,
    private val databaseService: DatabaseService
) : ViewModel() {

    private val _checkStates = mutableStateOf<Map<String, Boolean>>(emptyMap())
    val checkStates: State<Map<String, Boolean>> = _checkStates

    init {
        val allChecks = checksService.getAll()
        val newState = mutableMapOf<String, Boolean>()

        for (check in allChecks) {
            newState[check.getCheckId()] = databaseService.isCheckEnabled(check.getCheckId())
        }

        _checkStates.value = newState
    }

    fun updateCheckState(checkId: String, isEnabled: Boolean) {
        databaseService.setCheckEnabled(checkId, isEnabled)
        _checkStates.value = _checkStates.value.toMutableMap().apply {
            this[checkId] = isEnabled
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var perms: PermissionsService

    private val checksViewModel: ChecksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MITMDetectorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainScreen(checksViewModel)
                }
            }
        }

        perms.askForNotificationsPermissionIfNeeded(this)

        val intent = Intent("app.security.mitmdetector.ACTION_APP_STARTED")
        sendBroadcast(intent)
    }
}

@Composable
fun MainScreen(viewModel: ChecksViewModel) {
    val checkStates = viewModel.checkStates.value

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Text(
            text = "MITM Detector",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Available checks:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(checkStates.keys.toList()) { checkId ->
                val isChecked = checkStates[checkId] ?: false
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = checkId, modifier = Modifier.weight(1f))
                    Switch(
                        checked = isChecked,
                        onCheckedChange = { isChecked ->
                            viewModel.updateCheckState(checkId, isChecked)
                        }
                    )
                }
            }
        }
    }
}

