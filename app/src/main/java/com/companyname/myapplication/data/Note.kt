package com.companyname.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: Categories,
    val createdDate: Long = System.currentTimeMillis() // дефолтное значение для даты
)
