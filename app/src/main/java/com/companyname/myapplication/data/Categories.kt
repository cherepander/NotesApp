package com.companyname.myapplication.data

import androidx.compose.ui.graphics.Color

enum class Categories(val cname : String, val color: Color ) {

    PRIVATE("Личное", Color.Cyan),
    IDEAS("Идеи", Color.Yellow),
    JOB("Работа", Color.Magenta),
    URGENTLY("Срочно", Color.Red),
    OTHER("Другое", Color.Green)
}