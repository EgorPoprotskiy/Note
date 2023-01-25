package com.egorpoprotskiy.note.ViewModel

import androidx.lifecycle.*
import com.egorpoprotskiy.note.data.NoteDao
import com.egorpoprotskiy.note.model.Note
import kotlinx.coroutines.launch

// 14.1 Создаём модель представления заметочниа(создать файл/класс InveтtoryViewModel.kt)
class NoteViewModel(private val noteDao: NoteDao): ViewModel() {

    //18.1 Отображение сведений об элементе на экране с помощью адаптера, созданного в NoteListAdapter. getItems() - из NoteDao.kt
    val allItems: LiveData<List<Note>> = noteDao.getItems().asLiveData()
    // Далее в NoteListFragment

    // 14.5 добавь private функция называется insertNote() который принимает note объект и добавляет данные в базу данных неблокирующим образом.
    private fun insertNote(note: Note) {
        // 14.6 Чтобы взаимодействовать с базой данных вне основного потока, запустите сопрограмму и вызовите в ней метод DAO.
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

    // 14.7 В NoteViewModel добавьте еще одну закрытую функцию, которая принимает три строки и возвращает Note пример.
    private fun getNewNoteEntry(noteHeading: String, noteDescription: String, noteColor: String): Note {
        return Note(
            // переменные из БД(см файл Note)
            heading = noteHeading,
            description = noteDescription,
            color = noteColor,

        )
    }

    // 14.8 Все еще внутри NoteViewModel класс, добавьте общедоступную функцию с именем addNewNote() который принимает три строки для сведений об элементе.
    fun addNewNote(noteHeading: String, noteDescription: String, noteColor: String){
        val newNote = getNewNoteEntry(noteHeading, noteDescription, noteColor)
        // 14.9 Позвонить в insertNote() проходя в newNote для добавления нового объекта в базу данных. Это будет вызываться из фрагмента пользовательского интерфейса для добавления сведений об элементе в базу данных.
        insertNote(newNote)
    }

    // 14.10 Экран « Добавить элемент » содержит два текстовых поля для получения сведений о заметке от пользователя..
    // ..На этом шаге вы добавите функцию для проверки того, не является ли текст в TextFields пустым..
    // ..Вы будете использовать эту функцию для проверки ввода пользователя перед добавлением или обновлением сущности в базе данных. Эта проверка должна быть выполнена в ViewModel а не во Фрагменте.
    fun isEntryValid(noteHeading: String, noteDescription: String, noteColor: String): Boolean{
        if (noteHeading.isBlank() || noteDescription.isBlank()) {
            return false
        }
        return true
    }

    //20.1 Получаем сведения об элементе
    fun retrieveNote(id: Int): LiveData<Note> {
        //20.2 Внутри новой функции вызовите getItem()на noteDao, передавая параметр id. getItem() функция..
        // ..возвращает Flow. Чтобы потреблять Flow значение как LiveData вызов asLiveData() функцию и ..
        // ..используйте это как возврат retrieveNote()функция. Завершенная функция должна выглядеть следующим образом:(Далее в ItemDetailFragment)
        return noteDao.getItem(id).asLiveData()
    }

    //22.1 Удаление данных из БД (Далее в NoteDetailFragment) - НАЧАЛО УДАЛЕНИЯ ОБЪЕТОВ ИЗ БД
    fun deleteNote(note:Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    //26.1 Обновление данных именно в самой БД
    private fun getUpdatedNoteEntry(noteId: Int, noteHeading: String, noteDescription: String, noteColor: String): Note {
        return Note(
            id = noteId,
            heading = noteHeading,
            description = noteDescription,
            color = noteColor,
        )
    }

    //26.2 добавьте приватную функцию с именем updateNote() который принимает экземпляр класса сущности, Note
    private fun updateNote(note: Note) {
        viewModelScope.launch {
            noteDao.update(note)
        }
    }

    //26.3 передача информации об объекте (Далее в NoteAddFragment)
    fun updateNote(noteId: Int, noteHeading: String, noteDescription: String, noteColor: String) {
        val updateNote = getUpdatedNoteEntry(noteId, noteHeading, noteDescription, noteColor)
        updateNote(updateNote)
    }


}

// 14.2 добавьте NoteViewModelFactory класс для создания экземпляра NoteViewModel экземпляр.
class NoteViewModelFactory(private val noteDao: NoteDao): ViewModelProvider.Factory {
    // 14.3 переопределить create() метод внутри ViewModelProvider.Factory (ctrl+O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 14.4 Реализовать create() метод. Проверьте, modelClass такое же, как NoteViewModel класс и вернуть его экземпляр. В противном случае сгенерируйте исключение.
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}