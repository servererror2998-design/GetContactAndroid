package id.local.getcontact

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val credentials = SecureCredentialStore(this)
        val history = HistoryStore(this)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                LookupScreen(credentials, history)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LookupScreen(credentials: SecureCredentialStore, history: HistoryStore) {
    var number by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("Profil") }
    var output by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var showAccount by remember { mutableStateOf(false) }
    var historyText by remember { mutableStateOf(history.all().joinToString("\n") { "${it.mode} • ${it.number} • ${it.time}" }) }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(title = {
            Column {
                Text("Contact Lookup", fontWeight = FontWeight.Bold)
                Text("Android • local history", style = MaterialTheme.typography.labelSmall)
            }
        }, actions = { TextButton(onClick = { showAccount = !showAccount }) { Text("Akun") } })
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showAccount) {
                Text("Kredensial akun", style = MaterialTheme.typography.titleLarge)
                Text("Simpan hanya token/finalKey milik akun Anda. Jangan masukkan password.")
                var token by remember { mutableStateOf("") }
                var finalKey by remember { mutableStateOf("") }
                OutlinedTextField(token, { token = it }, Modifier.fillMaxWidth(), label = { Text("Session token") }, singleLine = true)
                OutlinedTextField(finalKey, { finalKey = it }, Modifier.fillMaxWidth(), label = { Text("finalKey") }, singleLine = true)
                Button(onClick = {
                    if (token.isNotBlank() && finalKey.isNotBlank()) {
                        credentials.save(token.trim(), finalKey.trim())
                        token = ""; finalKey = ""; showAccount = false
                        output = "Kredensial disimpan terenkripsi di perangkat."
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text("Simpan aman") }
                OutlinedButton(onClick = { credentials.clear(); output = "Kredensial dihapus." }, modifier = Modifier.fillMaxWidth()) { Text("Hapus kredensial") }
            }
            Text("Lookup nomor", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(number, { number = it }, Modifier.fillMaxWidth(), label = { Text("Nomor telepon") }, placeholder = { Text("+628xxxxxxxxxx") }, singleLine = true)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                listOf("Profil", "Tag", "Kuota").forEachIndexed { i, item ->
                    SegmentedButton(selected = mode == item, onClick = { mode = item }, shape = SegmentedButtonDefaults.itemShape(i, 3)) { Text(item) }
                }
            }
            Button(
                onClick = {
                    busy = true
                    scope.launch {
                        output = withContext(Dispatchers.IO) {
                            GetContactApi(credentials).run(mode, number.trim())
                        }
                        if (mode != "Kuota" && output.startsWith("API belum")) {
                            // Failed/unconfigured requests are not recorded as successful lookups.
                        } else if (mode != "Kuota") {
                            history.add(number.trim(), mode, output)
                            historyText = history.all().joinToString("\n") { "${it.mode} • ${it.number} • ${it.time}" }
                        }
                        busy = false
                    }
                },
                enabled = !busy && (mode == "Kuota" || number.isNotBlank()),
                modifier = Modifier.fillMaxWidth()
            ) { if (busy) CircularProgressIndicator(Modifier.size(18.dp)) else Text("Cari") }
            if (output.isNotBlank()) {
                Card(Modifier.fillMaxWidth()) { Text(output, Modifier.padding(16.dp)) }
            }
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Riwayat lokal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = { history.clear(); historyText = "" }) { Text("Hapus") }
            }
            Text(if (historyText.isBlank()) "Belum ada pencarian." else historyText)
            Text(
                "API privat belum terhubung. Antarmuka ini tidak mengklaim lookup nyata sampai ada integrasi yang diizinkan.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
