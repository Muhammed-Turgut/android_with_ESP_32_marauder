package com.muhammedturgut.esp32marauderapp.presentation.composables.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muhammedturgut.esp32marauderapp.R

@Composable
fun ToolPickerItem(title: String,
                   icon : Int,
                   active: Boolean,
                   onClick: () -> Unit){

    Box(modifier = Modifier
        .height(100.dp)
        .width(100.dp)
        .clickable{
            onClick()
        }
        .background(
            color = if(active) Color.White.copy(alpha = 0.15f)  else Color.Transparent,
            shape = RoundedCornerShape(16.dp))
        .border(
            width = if(active) 0.5.dp else 1.dp,
            color = if(active) Color.White else Color(0xFF63686F),
            shape = RoundedCornerShape(16.dp)
        ),
        contentAlignment = Alignment.Center
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = title,
                fontSize = 14.sp,
                fontFamily = if (active) FontFamily
                    (Font(R.font.inconsolata_semi_bold))
                else FontFamily(Font(R.font.inconsolata_light)),
                color = if(active) Color.White else Color(0xFF63686F)
            )
        }

    }

}