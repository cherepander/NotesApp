package com.companyname.myapplication



import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.companyname.myapplication.data.NoteDao
import com.companyname.myapplication.ui.AddNoteScreen
import com.companyname.myapplication.ui.EditNoteScreen
import com.companyname.myapplication.ui.NoteListScreen
import com.companyname.myapplication.ui.NoteViewModel
import com.companyname.myapplication.ui.NoteViewModelFactory

@Composable
fun NotesNavHost(noteDao: NoteDao) {
    val navController = rememberNavController()
    val viewModel: NoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = NoteViewModelFactory(noteDao)
    )

    NavHost(navController = navController, startDestination = "note_list") {

        composable("note_list") {
            NoteListScreen(navController = navController, viewModel = viewModel)
        }
        composable("add_note") { AddNoteScreen(navController, viewModel) }
        composable("edit_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toInt()
            noteId?.let { EditNoteScreen(navController, it, viewModel) }
        }
    }
}
