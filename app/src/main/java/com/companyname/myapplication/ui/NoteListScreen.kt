package com.companyname.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(navController: NavController, viewModel: NoteViewModel) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val notes by viewModel.sortedNotes.collectAsState()
    var currentNotes by remember { mutableStateOf(notes) }

    LaunchedEffect(notes, searchText) {
        currentNotes = if (searchText.text.isEmpty()) {
            notes
        } else {
            notes.filter { it.title.contains(searchText.text, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                actions = {
                    IconButton(onClick = { viewModel.toggleSortOrder() }) {
                        Text("Sort")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_note") }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            DragAndDropNoteList(
                notes = currentNotes,
                onReorder = { updatedNotes ->
                    currentNotes = updatedNotes
                    viewModel.reorderNotes(updatedNotes)
                },
                onDelete = { note ->
                    viewModel.deleteNote(note)
                },
                onEdit = { note ->
                    navController.navigate("edit_note/${note.id}")
                }
            )
        }
    }
}


