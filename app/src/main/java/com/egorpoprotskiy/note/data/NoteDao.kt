package com.egorpoprotskiy.note.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.egorpoprotskiy.note.model.Note
import kotlinx.coroutines.flow.Flow

// 12.1 в data пакет, создайте класс Kotlin Noteao.kt. Измените определение класса на interface и аннотировать с @Dao.
@Dao
interface NoteDao {
    // 12.2 Внутри тела интерфейса добавьте @Insert аннотация. OnConflictStrategy.IGNORE стратегия игнорирует новый элемент, если его первичный ключ уже находится в базе данных
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // добавьте insert() функция, которая принимает экземпляр Entity класс item как его аргумент.  Сделайте функцию функцией приостановки(suspend), чтобы эту функцию можно было вызывать из сопрограммы.
    suspend fun insert(note: Note)
    // 12.3 Обновление таблицы
    @Update
    suspend fun update(note: Note)
    // 12.4 Удаление элементов из таблицы
    @Delete
    suspend fun delete(note: Note)
    // 12.5 Query - это запрос SQLite для извлечения элемента из таблицы элементов. (В данном случае извлечение id)
    // Обратите внимание на :id. Вы используете двоеточие в запросе для ссылки на аргументы функции.
    @Query("SELECT * FROM note WHERE id = :id")
    /// Добавить getItem()функция, которая принимает Int аргумент и возвращает Flow<Item>.
    /// Room держит это Flow обновляется для вас, что означает, что вам нужно только один раз явно получить данные.
    fun getItem(id: Int): Flow<Note>
    // 12.6 Запрос на извлечение всех столбцов таблицы(по возрастанию)
    @Query("SELECT * FROM note ORDER BY heading2 ASC")
    // Room держит это Flow обновляется для вас, что означает, что вам нужно только один раз явно получить данные.
    fun getItems(): Flow<List<Note>>
}