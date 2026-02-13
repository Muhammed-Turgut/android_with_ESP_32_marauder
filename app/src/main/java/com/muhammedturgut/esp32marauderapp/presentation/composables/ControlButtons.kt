package com.muhammedturgut.esp32marauderapp.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ControlButtons(
    onScan: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(
            onClick = onScan,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("ON", color = Color.Black)
        }

        Button(
            onClick = onStop,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("OFF", color = Color.White)
        }
    }
}