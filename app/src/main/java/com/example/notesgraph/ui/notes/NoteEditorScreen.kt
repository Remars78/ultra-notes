package com.example.notesgraph.ui.notes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.notesgraph.data.NoteEntity
import com.example.notesgraph.ui.AppViewModel
import com.example.notesgraph.ui.draw.DrawingCanvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(vm: AppViewModel, noteId: Long, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var images by remember { mutableStateOf(listOf<String>()) }
    var showDraw by remember { mutableStateOf(false) }
    var loadedId by remember { mutableStateOf(noteId) }

    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            vm.loadNote(noteId)?.let {
                title = it.title; body = it.body
                images = it.imagePaths.split(";").filter { s -> s.isNotBlank() }
            }
        }
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { images = images + it.toString() } }

    fun save() {
        vm.saveNote(
            NoteEntity(
                id = loadedId,
                title = title,
                body = body,
                imagePaths = images.joinToString(";")
            )
        ) { loadedId = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == 0L) "Новая заметка" else "Заметка") },
                navigationIcon = {
                    IconButton(onClick = { save(); onBack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { picker.launch("image/*") }) {
                        Icon(Icons.Filled.Image, "Картинка")
                    }
                    IconButton(onClick = { showDraw = true }) {
                        Icon(Icons.Filled.Brush, "Рисовать")
                    }
                    IconButton(onClick = { save() }) {
                        Icon(Icons.Filled.Save, "Сохранить")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Заголовок") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = body, onValueChange = { body = it },
                label = { Text("Текст") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp)
            )
            Spacer(Modifier.height(12.dp))
            images.forEach { uri ->
                AsyncImage(
                    model = uri, contentDescription = null,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                )
            }
        }
    }

    if (showDraw) {
        DrawingCanvas(onClose = { showDraw = false })
    }
}
