package com.muhammedturgut.esp32marauderapp.presentation.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.muhammedturgut.esp32marauderapp.R
import com.muhammedturgut.esp32marauderapp.presentation.composables.common.ToolPickerItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onMenuDrawClick: () -> Unit,
    navController: NavController
) {

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded // başlangıçta yarı açık ✅
        )
    )

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 124.dp, // ✅ her zaman bu kadar açık kalır
        sheetContainerColor = Color(0xFF050505),
        containerColor = Color.Black,
        sheetDragHandle = null,
        sheetContent = {
            LogPanel(list)
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Column(modifier= Modifier
                .padding(16.dp)) {

                CustomAppBar(onMenuDrawClick ={
                    onMenuDrawClick()
                })

                Spacer(modifier = Modifier.height(16.dp))

                ProcessingUnitUsage(45,78)

                Spacer(modifier = Modifier.height(16.dp))

                UpTime(100)
                Spacer(modifier = Modifier.height(16.dp))
                ToolPicker(navController= navController)
            }
        }
    }


}

@Composable
fun ToolPicker(navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    val tools = listOf(
        Triple("WIFI TOOLS", R.drawable.wifi_icon, true),
        Triple("GPS TOOLS", R.drawable.gps_icon, false),
        Triple("HID TOOLS", R.drawable.hid_icon, false),
        Triple("NFC TOOLS", R.drawable.bluetooth_icon, false),
        Triple("BT TOOLS", R.drawable.nfc_icon, false),
        Triple("RF TOOLS", R.drawable.rf_icon, false),
        Triple("IR TOOLS", R.drawable.ir_icon, false),
        Triple("SERIAL MON", R.drawable.serial_mon_icon, false),
        Triple("FILES", R.drawable.file_icon, false),
        Triple("ANALYSIS", R.drawable.analysis_icon, false),
        Triple("GPIO PIN", R.drawable.gpio_icon, false),
        Triple("SETTINGS", R.drawable.settings_icon, false),
       )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier,
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
                    val view = when (item.first) {
                        "WIFI TOOLS"  -> "WifiTools"
                        "GPS TOOLS"   -> "GPSTools"
                        "HID TOOLS"   -> "HIDTools"
                        "NFC TOOLS"   -> "NFCTools"
                        "BT TOOLS"    -> "BTTools"
                        "RF TOOLS"    -> "RFTools"
                        "IR TOOLS"    -> "IRTools"
                        "SERIAL MON"  -> "SerialMon"
                        "FILES"       -> "Files"
                        "ANALYSIS"    -> "Analysis"
                        "GPIO PIN"    -> "GPIOPin"
                        else          -> "Settings"
                    }
                    navController.navigate(view)
                }
            )
        }
    }
}

@Composable
fun CustomAppBar(onMenuDrawClick : () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ){

        Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start){
        Icon(
            painter = painterResource(id = R.drawable.draw_menu_icon),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clickable{
                    onMenuDrawClick()
                },
            tint = Color.Unspecified
        )

       Spacer(modifier = Modifier.width(8.dp))

            Row() {
                Text(
                    "ESP-32", color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.inconsolata_semi_bold))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        "ONLINE",
                        color = Color(0xFF03C243),
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inconsolata_semi_bold))
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(modifier = Modifier
                        .width(12.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(color = Color(0xFF03C243))
                    )


                }
            }

     }




        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){

            Text("80%",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.inconsolata_light))
            )

            Spacer(modifier = Modifier.width(4.dp))

            BatteryIndicator(80)

        }

    }
}
@Composable
fun BatteryIndicator(percentage: Int) {
    val batteryWidth = 40.dp
    val batteryHeight = 20.dp

    Row(verticalAlignment = Alignment.CenterVertically) {
        // Ana batarya gövdesi
        Box(
            modifier = Modifier
                .width(batteryWidth)
                .height(batteryHeight)
                .border(
                    width = 1.dp,
                    color = Color(0xFF4C4C4C),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(2.dp)
        ) {
            // Doluluk çubuğu
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f) // 0f - 1f arası
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        color = when {
                            percentage > 50 -> Color(0xFF03C243) // yeşil
                            percentage > 20 -> Color(0xFFFFA500) // turuncu
                            else -> Color(0xFFFF0000)            // kırmızı
                        }
                    )
            )
        }

        // Batarya ucu (küçük çıkıntı)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                .background(Color(0xFF4C4C4C))
        )
    }
}

@Composable
fun ProcessingUnitUsage(RAMUsage : Int, CPUUsage : Int) {

    Row(modifier = Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {


        Box(modifier = Modifier
            .weight(1f)
            .border(
                width = 1.dp,
                color = Color(0xFF333333),
                shape = RoundedCornerShape(8.dp))
        ){
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start) {

                Text("CPU USAGE",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.inconsolata_regular))
                )

                Text("CPU: $CPUUsage%",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.inconsolata_semi_bold))
                )

                AnimatedProgressBar(
                    progress = CPUUsage/100f, // %75
                    color = Color(0xFF3B82F6),
                    height = 6.dp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier
            .weight(1f)
            .border(
                width = 1.dp,
                color = Color(0xFF333333),
                shape = RoundedCornerShape(8.dp))
        ){
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start) {

                Text("RAM USAGE",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.inconsolata_regular))
                )

                Text("RAM: $RAMUsage KB",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.inconsolata_semi_bold))
                )

                AnimatedProgressBar(
                    progress = RAMUsage/100f, // %75
                    color = Color(0xFF03C243),
                    height = 6.dp
                )
            }
        }

    }

}

@Composable
fun UpTime(time : Int) {

    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(color = Color(0xFF0A0A0A))
        .border(width = 1.dp,
            color = Color(0xFF333333),
            shape = RoundedCornerShape(8.dp)
        )
        .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            Text(
                "UPTIME",
                color = Color(0xFF6B7280),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.inconsolata_regular))
            )

            Text(
                "00.01.04.58 (D.H.M.S)",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.inconsolata_semi_bold))
            )
        }

    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPanel(
    list: List<LogListItem>,
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Drag handle + başlık
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(color = Color(0xFF63686F))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "RECENT ACTIVITY LOGS",
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.inconsolata_semi_bold))
                )
                Text(
                    "CLEAR LOGS",
                    color = Color(0xFF3B82F6),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.inconsolata_bold))
                )
            }
        }

        // Liste
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(com.muhammedturgut.esp32marauderapp.presentation.view.list) { item ->
                ListItem(time = item.time, type = item.type, content = item.content)
            }
        }
    }

}

@Composable
fun AnimatedProgressBar(
    progress: Float, // 0f - 1f arası
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF3B82F6),
    backgroundColor: Color = Color(0xFF1E1E1E),
    height: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(100.dp))
                .background(color)
        )
    }
}

@Composable
fun ListItem(time : String, type: String, content : String) {

    val color = when (type) {
        "INFO"     -> Color(0xFF03C243)  // yeşil
        "WARN"    -> Color(0xFFD20606)  // kırmızı
        "DEBUG"   -> Color(0xFF3B82F6)  // mavi
        "ERROR"   -> Color(0xFFFF4444)  // parlak kırmızı
        "ATTACK"  -> Color(0xFFFF6B00)  // turuncu
        "CAPTURE" -> Color(0xFFAB47BC)  // mor
        "SCAN"    -> Color(0xFF00BCD4)  // cyan
        "TX"      -> Color(0xFFFFEB3B)  // sarı
        "RX"      -> Color(0xFF8BC34A)  // açık yeşil
        "GPIO"    -> Color(0xFFFF9800)  // amber
        "SYS"     -> Color(0xFF9E9E9E)  // gri
        else      -> Color(0xFF63686F)
    }

    Row(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            time,
            color = Color(0xFF63686F),
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.inconsolata_regular)),
            lineHeight = 0.1.sp
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            "[${type}]",
            color = color,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.inconsolata_regular)),
            lineHeight = 0.1.sp

        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            content,
            color = Color(0xFF63686F),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.inconsolata_regular)),
            lineHeight = 14.sp
        )
    }
}


data class LogListItem(val time : String, val type : String, val content : String)

private val list: List<LogListItem> = listOf(
    LogListItem(time = "12:05:37:56", type = "INFO", content = "WiFi scan complete: 12 APs found on 2.4GHz"),
    LogListItem(time = "12:06:16:22", type = "WARN", content = "AP signal strength low: BSSID AA:BB:CC:DD:EE:FF"),
    LogListItem(time = "12:07:24:09", type = "ATTACK", content = "Deauth attack started on channel 6"),
    LogListItem(time = "12:08:52:45", type = "DEBUG", content = "Packet injection rate: 1200 pkt/s"),
    LogListItem(time = "12:09:58:44", type = "CAPTURE", content = "Target SSID: HomeNetwork_5G — handshake captured"),
    LogListItem(time = "12:10:01:24", type = "WARN", content = "GPS signal lost — last known: 41.0082° N, 28.9784° E"),
    LogListItem(time = "12:11:09:25", type = "SCAN", content = "Bluetooth scan: 7 devices discovered nearby"),
    LogListItem(time = "12:12:10:34", type = "ERROR", content = "Memory usage critical: 91% heap utilized"),
    LogListItem(time = "12:13:45:26", type = "CAPTURE", content = "NFC tag detected: ISO 14443-A, UID: 04:A3:2F:B1"),
    LogListItem(time = "12:14:23:06", type = "SYS", content = "Serial baud rate set to 115200 — connection stable"),
    LogListItem(time = "12:15:02:11", type = "TX", content = "IR signal transmitted: Samsung TV power toggle"),
    LogListItem(time = "12:15:44:33", type = "RX", content = "RF signal received: 433.92MHz, raw pulse captured"),
    LogListItem(time = "12:16:08:57", type = "GPIO", content = "Pin GPIO_2 state changed: LOW → HIGH"),
    LogListItem(time = "12:16:55:19", type = "SCAN", content = "WiFi scan started on channels 1-13"),
    LogListItem(time = "12:17:23:42", type = "ATTACK", content = "Evil portal launched: SSID spoofed as 'FreeAirport_WiFi'"),
    LogListItem(time = "12:18:01:05", type = "CAPTURE", content = "BLE advertisement captured: MAC 7C:2F:80:A1:44:BC"),
    LogListItem(time = "12:18:37:28", type = "TX", content = "Sub-GHz replay attack sent: 868MHz garage signal"),
    LogListItem(time = "12:19:14:50", type = "ERROR", content = "SD card read failed: file system corrupted"),
    LogListItem(time = "12:19:52:13", type = "SYS", content = "Device rebooted — uptime reset to 00:00:00"),
    LogListItem(time = "12:20:30:47", type = "INFO", content = "Firmware version: Marauder v0.13.5 loaded successfully"),
)

@Preview(showBackground = true)
@Composable
private fun Show(){
    val navController = rememberNavController()
   MainScreen(onMenuDrawClick ={

   }, navController = navController)
}
