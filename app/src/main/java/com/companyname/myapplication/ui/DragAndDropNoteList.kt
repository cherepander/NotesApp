// DragAndDropNoteList.kt
package com.companyname.myapplication.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.companyname.myapplication.data.Note
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DragAndDropNoteList(
    notes: List<Note>,
    onReorder: (List<Note>) -> Unit,
    onDelete: (Note) -> Unit,
    onEdit: (Note) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    var draggedNoteIndex by remember { mutableStateOf(-1) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    val reorderedNotes = notes.toMutableList()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(notes) { index, note ->
            val offsetY by animateDpAsState(targetValue = if (index == draggedNoteIndex) dragOffsetY.dp else 0.dp)
            val modifier = Modifier.offset(y = offsetY)

            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onEdit(note) }  // Открытие окна редактирования
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                draggedNoteIndex = index
                                dragOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetY += dragAmount.y / 6
                                val deltaIndex = (dragOffsetY / 30f).roundToInt()
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
                    },
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            IconButton(onClick = { onDelete(note) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                            }
                            Text(
                                text = note.category.cname,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier
                                    .background(note.category.color, shape = RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .clickable { onCategoryClick(note.category.cname) }  // Фильтрация при клике на категорию
                            )
                        }
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