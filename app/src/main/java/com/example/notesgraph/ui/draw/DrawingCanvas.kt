package com.example.notesgraph.ui.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingCanvas(onClose: () -> Unit) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var current by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Рисунок") },
                    actions = {
                        IconButton(onClick = { strokes.clear() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Очистить")
                        }
                        IconButton(onClick = onClose) {
                            Icon(Icons.Filled.Check, contentDescription = "Готово")
                        }
                    }
                )
            }
        ) { pad ->
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad)
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { current = listOf(it) },
                            onDrag = { change, _ -> current = current + change.position },
                            onDragEnd = {
                                strokes.add(current)
                                current = emptyList()
                            }
                        )
                    }
            ) {
                val allStrokes = strokes + listOf(current)
                allStrokes.forEach { pts ->
                    if (pts.size > 1) {
                        val path = Path()
                        path.moveTo(pts.first().x, pts.first().y)
                        for (i in 1 until pts.size) {
                            path.lineTo(pts[i].x, pts[i].y)
                        }
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = 6f, cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }
