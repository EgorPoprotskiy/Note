package com.egorpoprotskiy.note.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// СОЗДАЕМ СУЩНОСТЬ ЭЛЕМЕНТА

// 10.1 Над Item объявление класса, аннотируйте класс данных с помощью @Entity. Использовать tableName аргумент, чтобы дать item как имя таблицы SQLite.
@Entity(tableName = "note")
// 10.2 Внутри data пакет, создайте класс Kotlin с именем Note. Этот класс будет представлять сущность базы данных в вашем приложении. Также указать первичный конструктор.
data class Note (
    // 10.3 Добавить аннотации
    ///autoGenerate = true - Room генерирует идентификатор для каждой сущности.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "heading2")
    val heading: String,
    @ColumnInfo(name = "desctiption2")
    val description: String,
    @ColumnInfo(name = "color")
    val color: String,
)




