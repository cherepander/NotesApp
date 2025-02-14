package com.companyname.myapplication.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.companyname.myapplication.data.Note
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DragAndDropNoteList(
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
                    .padding(8.dp)
                    .background(if (index == draggedNoteIndex) Color.LightGray else Color.White, shape = RoundedCornerShape(10.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                draggedNoteIndex = index
                                dragOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
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
                    Column( modifier = Modifier.weight(1f)){
                        Text(text = note.title, modifier = Modifier.padding(bottom = 12.dp),fontSize = 25.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)

                        Text(text = note.content, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis )
                        }
                    Column(){

                        IconButton(onClick = { onDelete(note) }  ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                        }
                        Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentWidth()
                                .background(
                                    color = note.category.color,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 1.dp ),
                            text = note.category.cname,
                            fontSize = 15.sp,
                            overflow = TextOverflow.Ellipsis
                        )
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