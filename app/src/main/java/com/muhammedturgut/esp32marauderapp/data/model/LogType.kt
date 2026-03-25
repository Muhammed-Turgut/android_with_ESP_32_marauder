package com.muhammedturgut.esp32marauderapp.data.model

enum class LogType(val label : String){
    WIFI("wifi"),
    HID("hid"),
    RF("rf"),
    IF("if"),
    BT("bluetooth"),
    GPS("gps"),
    NFC("nfc"),
    SERIAL("Serial_Mon"),
    FILES("files"),
    ANALYSIS("analysis"),
    GPIO("gpio"),
    SETTINGS("settings")
}