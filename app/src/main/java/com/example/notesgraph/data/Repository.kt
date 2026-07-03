package com.example.notesgraph.data

class Repository(private val db: AppDatabase) {
    // Notes
    val notes = db.noteDao().observeAll()
    suspend fun getNote(id: Long) = db.noteDao().getById(id)
    suspend fun saveNote(note: NoteEntity) = db.noteDao().upsert(note)
    suspend fun deleteNote(note: NoteEntity) = db.noteDao().delete(note)

    // Blocks
    val blocks = db.blockDao().observeAll()
    suspend fun saveBlock(block: BlockEntity) = db.blockDao().upsert(block)
    suspend fun updateBlock(block: BlockEntity) = db.blockDao().update(block)
    suspend fun deleteBlock(id: Long) {
        db.linkDao().deleteByBlock(id)
        db.blockDao().deleteById(id)
    }

    // Links
    val links = db.linkDao().observeAll()
    suspend fun saveLink(link: LinkEntity) = db.linkDao().upsert(link)
    suspend fun deleteLink(id: Long) = db.linkDao().deleteById(id)
}
