package com.muhammedturgut.esp32marauderapp.core.config

data class touchFromat(
    val type: String,
    val status : String,
    val message : String,
    val content : String,

)



//Serial.println("{\"type\":\"status\",\"msg\":\"ESP32 ready\"}");