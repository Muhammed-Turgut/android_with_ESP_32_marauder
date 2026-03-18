package com.muhammedturgut.esp32marauderapp.core.appNavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.muhammedturgut.esp32marauderapp.presentation.view.DrawerMenu
import com.muhammedturgut.esp32marauderapp.presentation.view.MainScreen
import com.muhammedturgut.esp32marauderapp.presentation.view.SplashScreen
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.AnalysisToolsView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.BTToolsView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.FileManagerView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.GPIOPinToolView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.GPSToolsView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.HIDToolsView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.IRToolsView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.NFCToolsView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.RFToolsView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.SerialMonitorView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.SettingsView
import com.muhammedturgut.esp32marauderapp.presentation.view.ToolsMainScreens.WifiToolsView
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.background(color = Color.Black),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(240.dp),
                drawerShape = RoundedCornerShape(topEnd = 0.dp, topStart = 0.dp)
            ){
                DrawerMenu()
            }
        }
    ) {

        NavHost(
            navController = navController,
            startDestination = "SplashScreen",
            modifier = modifier.background(color = Color.Black)
        ) {

            composable("SplashScreen") {
                SplashScreen(navController = navController)
            }

            composable("HomeScreen") {
                MainScreen(onMenuDrawClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }, navController = navController)
            }

            composable("WifiTools") {
                WifiToolsView()
            }

            composable("GPSTools") {
                GPSToolsView()
            }

            composable("HIDTools") {
                HIDToolsView()
            }

            composable("NFCTools") {
                NFCToolsView()
            }

            composable("BTTools") {
                BTToolsView()
            }

            composable("RFTools") {
                RFToolsView()
            }

            composable("IRTools") {
                IRToolsView()
            }

            composable("SerialMon") {
                SerialMonitorView()
            }

            composable("Files") {
                FileManagerView()
            }

            composable("Analysis") {
                AnalysisToolsView()
            }

            composable("GPIOPin") {
                GPIOPinToolView()
            }

            composable("Settings") {
                SettingsView()
            }
        }

    }


}
