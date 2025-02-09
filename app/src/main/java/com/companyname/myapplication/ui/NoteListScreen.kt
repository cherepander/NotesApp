package com.companyname.myapplication.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.companyname.myapplication.data.Note
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(navController: NavController, viewModel: NoteViewModel) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val notes by viewModel.sortedNotes.collectAsState()
    var currentNotes by remember { mutableStateOf(notes) }

    LaunchedEffect(notes) {
        currentNotes = notes
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

            DraggableNoteList(
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

@Composable
fun DraggableNoteList(
    notes: List<Note>,
    onReorder: (List<Note>) -> Unit,
    onDelete: (Note) -> Unit,
    onEdit: (Note) -> Unit
) {
    var draggedNoteIndex by remember { mutableStateOf(-1) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    val reorderedNotes = notes.toMutableList()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(notes) { index, note ->
            val offsetY by animateDpAsState(targetValue = if (index == draggedNoteIndex) dragOffsetY.dp else 0.dp)
            val modifier = Modifier.offset(y = offsetY)

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(if (index == draggedNoteIndex) Color.LightGray else Color.Transparent)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                draggedNoteIndex = index
                                dragOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                // Уменьшаем чувствительность в 3 раза
                                dragOffsetY += dragAmount.y / 6

                                // Порог для перестановки элементов
                                val dragThreshold = 30f
                                val deltaIndex = (dragOffsetY / dragThreshold).roundToInt()
                                val targetIndex = (draggedNoteIndex + deltaIndex).coerceIn(0, notes.lastIndex)

                                if (targetIndex != draggedNoteIndex && draggedNoteIndex in notes.indices) {
                                    reorderedNotes.swap(draggedNoteIndex, targetIndex)
                                    onReorder(reorderedNotes)
                                    draggedNoteIndex = targetIndex
                                    dragOffsetY = 0f
                                }
                            },
                            onDragEnd = {
                                draggedNoteIndex = -1
                                dragOffsetY = 0f
                            }
                        )
                    }
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEdit(note) }
                ) {
                    Text(text = note.title, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onDelete(note) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

private fun MutableList<Note>.swap(index1: Int, index2: Int) {
    if (index1 in indices && index2 in indices) {
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }
}