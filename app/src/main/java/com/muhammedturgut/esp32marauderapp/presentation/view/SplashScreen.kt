package com.muhammedturgut.esp32marauderapp.presentation.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.muhammedturgut.esp32marauderapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){

    LaunchedEffect(Unit) {
        delay(3000) // 3 saniye
        navController.navigate("HomeScreen") {
            popUpTo("SplashScreen") { inclusive = true }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Black)
     ){
        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Image(painter = painterResource(R.drawable.mirsad_app_logo),
                 contentDescription = null,
                modifier = Modifier.size(width = 216.dp, height = 52.dp))

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "The Ultimate Android Marauder Interface",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.inconsolata_light))
            )


        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Icon(
                painter = painterResource(id = R.drawable.usb_connecting_icon),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "The Ultimate Android Marauder Interface",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.inconsolata_medium))
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Show(){
    val navController = rememberNavController()
    SplashScreen(navController)
}