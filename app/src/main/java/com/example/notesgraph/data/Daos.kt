package com.example.notesgraph.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity): Long

    @Delete
    suspend fun delete(note: NoteEntity)
}

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocks")
    fun observeAll(): Flow<List<BlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(block: BlockEntity): Long

    @Update
    suspend fun update(block: BlockEntity)

    @Query("DELETE FROM blocks WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface LinkDao {
    @Query("SELECT * FROM links")
    fun observeAll(): Flow<List<LinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(link: LinkEntity): Long

    @Query("DELETE FROM links WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM links WHERE fromId = :blockId OR toId = :blockId")
    suspend fun deleteByBlock(blockId: Long)
}
