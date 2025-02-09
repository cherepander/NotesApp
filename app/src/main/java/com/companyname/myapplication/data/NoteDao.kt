
package com.companyname.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // Получение всех заметок с использованием Flow
    @Query("SELECT * FROM note_table ORDER BY createdDate DESC")
    fun getAllNotes(): Flow<List<Note>>

    // Вставка заметки
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    // Удаление заметки
    @Delete
    suspend fun delete(note: Note)

    // Обновление заметки
    @Update
    suspend fun update(note: Note)

    @Update
    suspend fun updateAll(notes: List<Note>)

    @Query("SELECT * FROM note_table WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Query("SELECT * FROM note_table ORDER BY createdDate DESC")
    fun getAllNotesByDate(): Flow<List<Note>>

    @Query("SELECT * FROM note_table ORDER BY title ASC")
    fun getAllNotesByTitle(): Flow<List<Note>>

}
