package com.companyname.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.companyname.myapplication.data.NoteDatabase
import com.companyname.myapplication.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = NoteDatabase.getDatabase(applicationContext)
        val noteDao = database.noteDao()

        setContent {
            NotesAppTheme {
                NotesNavHost(noteDao = noteDao)
            }
        }
    }
}
