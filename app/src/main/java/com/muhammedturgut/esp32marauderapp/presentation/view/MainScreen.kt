package com.muhammedturgut.esp32marauderapp.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muhammedturgut.esp32marauderapp.R
import com.muhammedturgut.esp32marauderapp.presentation.composables.common.ToolPickerItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onMenuDrawClick: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Top
    ){

        CustomAppBar(onMenuDrawClick ={
            onMenuDrawClick()
        })

        ToolPicker()

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
            .height(0.5.dp)
            .background(Color.White))



     }
}

@Composable
fun ToolPicker() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val tools = listOf(
        Triple("Wifi", R.drawable.wifi_icon, true),
        Triple("GPS", R.drawable.gps_icon, false),
        Triple("HID", R.drawable.hid_icon, false),
        Triple("Bluetooth", R.drawable.bluetooth_icon, false),
        Triple("NFC", R.drawable.nfc_icon, false),
        Triple("RF", R.drawable.rf_icon, false),
        Triple("IR", R.drawable.ir_icon, false),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tools.size) { index ->
            val item = tools[index]
            ToolPickerItem(
                title = item.first,
                icon = item.second,
                active = index == selectedIndex,
                onClick = {
                    selectedIndex = index
                }
            )
        }
    }
}

@Composable
fun CustomAppBar(onMenuDrawClick : () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ){

        Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start){
        Icon(
            painter = painterResource(id = R.drawable.menu_icon),
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clickable{
                    onMenuDrawClick()
                },
            tint = Color.Unspecified
        )

       Spacer(modifier = Modifier.width(8.dp))

        Text("Connected",
            color = Color.White,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_regular))
        )
     }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            Icon(
                painter = painterResource(id = R.drawable.gps_icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("GPS",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.fredoka_light))
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun Show(){
   // MainScreen()
}
