package com.companyname.myapplication.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.companyname.myapplication.data.Note
import com.companyname.myapplication.data.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {

    private val _sortAscending = MutableStateFlow(true)
    private val _allNotes = noteDao.getAllNotes()

    val sortedNotes: StateFlow<List<Note>> = combine(_sortAscending, _allNotes) { isAscending, notes ->
        if (isAscending) {
            notes.sortedBy { it.title }
        } else {
            notes.sortedByDescending { it.title }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
    }
    fun reorderNotes(newList: List<Note>) {
        viewModelScope.launch {
            noteDao.updateAll(newList)
        }
    }

    fun updateNotes(updatedNotes: List<Note>) {
        viewModelScope.launch {
            noteDao.updateAll(updatedNotes)
        }
    }
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }


    fun addNote(note: Note) {
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

}


enum class SortOrder {
    BY_DATE, BY_TITLE
}

class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
