package com.egorpoprotskiy.note

import android.app.Application
import com.egorpoprotskiy.note.data.NoteRoomDatabase

// 15.1 создать val называется database типа NoteRoomDatabase. Создайте экземпляр database экземпляр, позвонив getDatabase() на NoteRoomDatabase проходит в контексте..
// ..Использовать lazy делегировать, чтобы экземпляр database лениво создается, когда вам впервые нужна/доступна ссылка (а не при запуске приложения)..
// ..Это создаст базу данных (физическую базу данных на диске) при первом доступе.
class NoteApplication: Application() {
    val database: NoteRoomDatabase by lazy { NoteRoomDatabase.getDatabase(this) }
    //Вы будете использовать это database экземпляр позже в кодовой лаборатории при создании экземпляра ViewModel.
}