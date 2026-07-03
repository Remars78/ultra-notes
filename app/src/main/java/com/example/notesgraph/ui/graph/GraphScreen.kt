package com.example.notesgraph.ui.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.notesgraph.data.BlockEntity
import com.example.notesgraph.ui.AppViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(vm: AppViewModel) {
    val blocks by vm.blocks.collectAsState()
    val links by vm.links.collectAsState()

    // Пан всего полотна
    var canvasOffset by remember { mutableStateOf(Offset.Zero) }

    // Режим соединения: id первого выбранного блока
    var linkFrom by remember { mutableStateOf<Long?>(null) }
    var linkMode by remember { mutableStateOf(false) }

    var editing by remember { mutableStateOf<BlockEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (linkMode) "Связь: выберите блоки" else "Связи") },
                actions = {
                    FilterChip(
                        selected = linkMode,
                        onClick = { linkMode = !linkMode; linkFrom = null },
                        label = { Text("Соединять") },
                        leadingIcon = { Icon(Icons.Filled.Link, null) }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Новый блок в центре текущего вида
                vm.addBlock(-canvasOffset.x + 300f, -canvasOffset.y + 400f)
            }) { Icon(Icons.Filled.Add, null) }
        }
    ) { pad ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .background(Color(0xFF15151C))
                .pointerInput(Unit) {
                    detectDragGestures { _, drag -> canvasOffset += drag }
                }
        ) {
            // Слой связей
            Canvas(Modifier.fillMaxSize()) {
                val byId = blocks.associateBy { it.id }
                links.forEach { link ->
                    val a = byId[link.fromId] ?: return@forEach
                    val b = byId[link.toId] ?: return@forEach
                    val start = Offset(a.x + a.width / 2, a.y + a.height / 2) + canvasOffset
                    val end = Offset(b.x + b.width / 2, b.y + b.height / 2) + canvasOffset
                    drawLine(Color(0xFF7C7CF0), start, end, strokeWidth = 4f)
                }
            }

            // Слой блоков
            blocks.forEach { block ->
                BlockCard(
                    block = block,
                    canvasOffset = canvasOffset,
                    highlighted = linkFrom == block.id,
                    onDrag = { dx, dy ->
                        if (!linkMode) vm.updateBlock(block.copy(x = block.x + dx, y = block.y + dy))
                    },
                    onTap = {
                        if (linkMode) {
                            val from = linkFrom
                            if (from == null) linkFrom = block.id
                            else { vm.addLink(from, block.id); linkFrom = null }
                        } else editing = block
                    }
                )
            }
        }
    }

    editing?.let { blk ->
        BlockEditDialog(
            block = blk,
            onDismiss = { editing = null },
            onSave = { vm.updateBlock(it); editing = null },
            onDelete = { vm.deleteBlock(blk.id); editing = null }
        )
    }
}

@Composable
private fun BlockCard(
    block: BlockEntity,
    canvasOffset: Offset,
    highlighted: Boolean,
    onDrag: (Float, Float) -> Unit,
    onTap: () -> Unit
) {
    Box(
        Modifier
            .offset {
                IntOffset(
                    (block.x + canvasOffset.x).roundToInt(),
                    (block.y + canvasOffset.y).roundToInt()
                )
            }
            .size(block.width.dp, block.height.dp)
            .pointerInput(block.id) {
                detectTapGestures(onTap = { onTap() })
            }
            .pointerInput(block.id) {
                detectDragGestures { _, drag -> onDrag(drag.x, drag.y) }
            }
    ) {
        ElevatedCard(
            Modifier.fillMaxSize(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (highlighted) Color(0xFF3A3A66) else Color(block.color)
            )
        ) {
            Column(Modifier.fillMaxSize().padding(12.dp)) {
                Text(block.title.ifBlank { "Блок" }, color = Color.White, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(6.dp))
                Text(block.content, color = Color(0xFFCFCFE0), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlockEditDialog(
    block: BlockEntity,
    onDismiss: () -> Unit,
    onSave: (BlockEntity) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(block.title) }
    var content by remember { mutableStateOf(block.content) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onSave(block.copy(title = title, content = content)) }) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDelete) { Text("Удалить") } },
        title = { Text("Блок") },
        text = {
            Column {
                OutlinedTextField(title, { title = it }, label = { Text("Заголовок") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(content, { content = it }, label = { Text("Содержимое") })
            }
        }
    )
}
