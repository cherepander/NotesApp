package com.companyname.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
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
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Получаем уникальные категории для фильтра
    val categories = notes.map { it.category.cname }.distinct()

    LaunchedEffect(notes, searchText, selectedCategory) {
        currentNotes = notes.filter {
            (searchText.text.isEmpty() || it.title.contains(searchText.text, ignoreCase = true)) &&
                    (selectedCategory == null || it.category.cname == selectedCategory)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notes", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton(onClick = { viewModel.toggleSortOrder() }) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "Sort")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_note") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )

            // Категории в виде чипсов с горизонтальной прокруткой
            LazyRow(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                item {
                    AssistChip(
                        onClick = { selectedCategory = null },
                        label = { Text("All") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedCategory == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                // Используем items из LazyRow для отображения списка категорий
                items(categories.size) { index ->
                    val category = categories[index]
                    AssistChip(
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedCategory == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }


            DragAndDropNoteList(
                notes = currentNotes,
                onReorder = { updatedNotes ->
                    currentNotes = updatedNotes
                    viewModel.reorderNotes(updatedNotes)
                },
                onDelete = { note ->
                    viewModel.deleteNote(note)
                    // Обновляем текущий список заметок после удаления
                    currentNotes = currentNotes.filter { it.id != note.id }
                },
                onEdit = { note ->
                    navController.navigate("edit_note/${note.id}")
                },
                onCategoryClick = { category ->
                    // Обработчик клика на категорию заметки
                    selectedCategory = category
                }
            )
        }
    }
}