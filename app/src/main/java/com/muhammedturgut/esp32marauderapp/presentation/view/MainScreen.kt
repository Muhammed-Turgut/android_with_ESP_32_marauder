package com.muhammedturgut.esp32marauderapp.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muhammedturgut.esp32marauderapp.data.model.WifiNetwork
import com.muhammedturgut.esp32marauderapp.presentation.composables.ControlButtons
import com.muhammedturgut.esp32marauderapp.presentation.viewModel.UsbViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun MainScreen(
    viewModel: UsbViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        StatusBar(viewModel.status)

        Spacer(Modifier.height(12.dp))

        WifiList(viewModel.wifiList)

        Spacer(Modifier.weight(1f))

       // ControlButtons(onScan, onStop)
    }
}

@Composable
fun StatusBar(text: String) {
    Text(
        text = "Durum: $text",
        color = Color.Green,
        fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun WifiList(list: List<WifiNetwork>) {
    LazyColumn {
        items(list) { wifi ->
            WifiItem(wifi)
        }
    }
}

@Composable
fun WifiItem(wifi: WifiNetwork) {
    val color = when {
        wifi.rssi > -50 -> Color.Green
        wifi.rssi > -70 -> Color.Yellow
        else -> Color.Red
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = wifi.ssid,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${wifi.rssi} dBm",
            color = color
        )
    }
}