package com.example.notesgraph.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesgraph.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(AppDatabase.get(app))

    val notes = repo.notes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val blocks = repo.blocks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val links = repo.links.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Notes ---
    fun saveNote(note: NoteEntity, onSaved: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repo.saveNote(note.copy(updatedAt = System.currentTimeMillis()))
        onSaved(id)
    }
    fun deleteNote(note: NoteEntity) = viewModelScope.launch { repo.deleteNote(note) }
    suspend fun loadNote(id: Long) = repo.getNote(id)

    // --- Blocks ---
    fun addBlock(x: Float, y: Float) = viewModelScope.launch {
        repo.saveBlock(BlockEntity(title = "Новый блок", x = x, y = y))
    }
    fun updateBlock(block: BlockEntity) = viewModelScope.launch { repo.updateBlock(block) }
    fun deleteBlock(id: Long) = viewModelScope.launch { repo.deleteBlock(id) }

    // --- Links ---
    fun addLink(fromId: Long, toId: Long) = viewModelScope.launch {
        if (fromId != toId) repo.saveLink(LinkEntity(fromId = fromId, toId = toId))
    }
    fun deleteLink(id: Long) = viewModelScope.launch { repo.deleteLink(id) }
}
