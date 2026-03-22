package com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import com.muhammedturgut.esp32marauderapp.presentation.viewModel.WifiToolsViewModel
import kotlinx.coroutines.delay

@Composable
fun WifiToolsView(
    viewModel: WifiToolsViewModel = hiltViewModel()
) {
    val wifiNetworks by viewModel.wifiNetworks.collectAsState()

    LaunchedEffect(Unit) {
        delay(2000)              // port açılmasını bekle
        viewModel.startWifiScan()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Başlık
        Text(
            text = "WiFi Tarama",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Butonlar
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { viewModel.startWifiScan() }) {
                Text("Tara")
            }
            Button(onClick = { viewModel.stopWifiScan() }) {
                Text("Durdur")
            }
            OutlinedButton(onClick = { viewModel.clearList() }) {
                Text("Temizle")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liste boşsa
        if (wifiNetworks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ağ bulunamadı",
                    color = Color.Gray
                )
            }
        } else {
            // Ağ listesi
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wifiNetworks) { network ->
                    WifiNetworkCard(network = network)
                }
            }
        }
    }
}

@Composable
fun WifiNetworkCard(network: WifiNetwork) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = network.ssid,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
            Text(
                text = "${network.rssi} dBm",
                color = rssiColor(network.rssi)
            )
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