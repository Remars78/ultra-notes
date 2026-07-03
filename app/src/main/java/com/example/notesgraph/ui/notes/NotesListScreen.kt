package com.example.notesgraph.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.notesgraph.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(vm: AppViewModel, onOpen: (Long) -> Unit, onNew: () -> Unit) {
    val notes by vm.notes.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Заметки") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNew) { Icon(Icons.Filled.Add, null) }
        }
    ) { pad ->
        if (notes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Пока пусто. Нажмите + чтобы создать заметку.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(12.dp)) {
                items(notes, key = { it.id }) { note ->
                    ElevatedCard(
                        Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onOpen(note.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                note.title.ifBlank { "Без названия" },
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (note.body.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(note.body, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}
