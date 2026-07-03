package com.example.notesgraph.ui.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/** Простой холст для рисования от руки. Точки хранятся как список штрихов. */
@Composable
fun DrawingCanvas(onClose: () -> Unit) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var current by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("Рисунок") },
                    actions = {
                        IconButton(onClick = { strokes.clear() }) { Icon(Icons.Filled.Delete, null) }
                        IconButton(onClick = onClose) { Icon(Icons.Filled.Check, null) }
                    }
                )
            }
        ) { pad ->
            Canvas(
                Modifier.fillMaxSize().padding(pad).background(Color.White)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { current = listOf(it) },
                            onDrag = { change, _ -> current = current + change.position },
                            onDragEnd = { strokes.add(current); current = emptyList() }
                        )
                    }
            ) {
                (strokes + listOf(current)).forEach { pts ->
                    if (pts.size > 1) {
                        val path = Path().apply {
                            moveTo(pts.first().x, pts.first().y)
                            pts.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(path, Color.Black, style = Stroke(width = 6f, cap = StrokeCap.Round))
                    }
                }
            }
        }
    }
}
