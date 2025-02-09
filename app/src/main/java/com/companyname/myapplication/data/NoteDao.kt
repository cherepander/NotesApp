
package com.companyname.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // Получение всех заметок с использованием Flow для реактивного обновления
    @Query("SELECT * FROM note_table ORDER BY createdDate DESC")
    fun getAllNotes(): Flow<List<Note>>

    // Вставка заметки (suspend)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    // Удаление заметки (suspend)
    @Delete
    suspend fun delete(note: Note)


    @Update
    suspend fun update(note: Note)

    @Update
    suspend fun updateAll(notes: List<Note>)

    // Получение заметки по ID (suspend)
    @Query("SELECT * FROM note_table WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?


    @Query("SELECT * FROM note_table ORDER BY createdDate DESC")
    fun getAllNotesByDate(): Flow<List<Note>>

    @Query("SELECT * FROM note_table ORDER BY title ASC")
    fun getAllNotesByTitle(): Flow<List<Note>>

}
