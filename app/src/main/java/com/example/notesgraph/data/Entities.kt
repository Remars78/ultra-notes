package com.example.notesgraph.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Заметка — контейнер обычного режима (текст, картинки, рисунок).
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val body: String = "",
    /** Пути к изображениям через ';' */
    val imagePaths: String = "",
    /** Путь к сохранённому рисунку (PNG), либо пусто */
    val drawingPath: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Блок в режиме связей. Хранит произвольную информацию и координаты на canvas.
 */
@Entity(tableName = "blocks")
data class BlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val content: String = "",
    /** Тип блока для кастомизации: text, task, image, ... */
    val type: String = "text",
    /** Цвет ARGB */
    val color: Long = 0xFF2D2D3A,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 220f,
    val height: Float = 140f,
    /** Опциональная привязка к заметке */
    val noteId: Long? = null
)

/**
 * Связь между двумя блоками (направленная, с подписью).
 */
@Entity(
    tableName = "links",
    indices = [Index("fromId"), Index("toId")]
)
data class LinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromId: Long,
    val toId: Long,
    val label: String = "",
    /** Тип связи для кастомизации стрелок/стиля */
    val type: String = "default"
)
