package com.example.notesgraph.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notesgraph.ui.notes.NoteEditorScreen
import com.example.notesgraph.ui.notes.NotesListScreen
import com.example.notesgraph.ui.graph.GraphScreen

@Composable
fun AppRoot() {
    val vm: AppViewModel = viewModel()
    val nav = rememberNavController()
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0; nav.navigate("notes") { launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.Notes, null) },
                    label = { Text("Заметки") }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1; nav.navigate("graph") { launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.AccountTree, null) },
                    label = { Text("Связи") }
                )
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = "notes", modifier = Modifier.padding(pad)) {
            composable("notes") {
                NotesListScreen(vm,
                    onOpen = { id -> nav.navigate("editor/$id") },
                    onNew = { nav.navigate("editor/0") })
            }
            composable("editor/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull() ?: 0L
                NoteEditorScreen(vm, noteId = id, onBack = { nav.popBackStack() })
            }
            composable("graph") { GraphScreen(vm) }
        }
    }
}
