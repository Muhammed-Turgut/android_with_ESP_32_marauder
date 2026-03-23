package com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import com.muhammedturgut.esp32marauderapp.presentation.viewModel.WifiToolsViewModel
import kotlinx.coroutines.delay

@Composable
fun WifiToolsView(
    viewModel: WifiToolsViewModel = hiltViewModel()
) {
    val wifiNetworks by viewModel.wifiNetworks.collectAsState()
    val debugLogs by viewModel.debugLogs.collectAsState()

    var showDebug by remember { mutableStateOf(true) }
    var isScanning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔥 HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "WiFi Tarama",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            if (isScanning) {
                Text("● SCANNING", color = Color(0xFF00FFAA))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔥 BUTTONS
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                isScanning = true
                viewModel.startWifiScan()
            }) {
                Text("Tara")
            }

            Button(onClick = {
                isScanning = false
                viewModel.stopWifiScan()
            }) {
                Text("Durdur")
            }

            OutlinedButton(onClick = { viewModel.clearList() }) {
                Text("Temizle")
            }

            OutlinedButton(onClick = { showDebug = !showDebug }) {
                Text(if (showDebug) "Log Gizle" else "Log Göster")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔥 NETWORK SAYISI
        Text(
            text = "Bulunan ağ: ${wifiNetworks.size}",
            color = Color.Gray,
            fontSize = 12.sp
        )

        // 🔥 DEBUG PANEL
        if (showDebug) {
            DebugPanel(debugLogs)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔥 LİSTE
        if (wifiNetworks.isEmpty()) {
            EmptyState(isScanning)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = wifiNetworks,
                    key = { it.ssid } // 🔥 önemli performans fix
                ) { network ->
                    WifiNetworkCard(network)
                }
            }
        }
    }
}

@Composable
fun EmptyState(isScanning: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isScanning) "Taranıyor..." else "Ağ bulunamadı",
            color = Color.Gray
        )
    }
}

@Composable
fun DebugPanel(logs: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D))
    ) {
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            reverseLayout = true
        ) {
            items(logs) { log ->
                Text(
                    text = log,
                    color = when {
                        log.contains("❌") -> Color(0xFFFF5555)
                        log.contains("✅") -> Color(0xFF55FF55)
                        else -> Color(0xFFAAAAAA)
                    },
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
@Composable
fun WifiNetworkCard(network: WifiNetwork) {

    val strength = when {
        network.rssi >= -50 -> "Güçlü"
        network.rssi >= -70 -> "Orta"
        else -> "Zayıf"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = network.ssid,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${network.rssi} dBm",
                    color = rssiColor(network.rssi)
                )

                Text(
                    text = strength,
                    color = rssiColor(network.rssi),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Sinyal gücüne göre renk
@Composable
fun rssiColor(rssi: Int): Color {
    return when {
        rssi >= -50 -> Color(0xFF4CAF50)   // güçlü — yeşil
        rssi >= -70 -> Color(0xFFFFC107)   // orta — sarı
        else        -> Color(0xFFF44336)   // zayıf — kırmızı
    }
}