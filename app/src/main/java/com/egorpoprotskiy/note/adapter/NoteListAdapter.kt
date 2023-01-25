package com.egorpoprotskiy.note.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egorpoprotskiy.note.databinding.ItemNoteBinding
import com.egorpoprotskiy.note.model.Note


// 17.1 Добавляем адаптер для RecyclerView(создать класс/файл NoteListAdapter.kt)
class NoteListAdapter (private val onNoteClicked: (Note) -> Unit): ListAdapter<Note, NoteListAdapter.NoteViewHolder>(DiffCallback) {

    //17.2 Создание класса ItemViewHolder
    class NoteViewHolder (private var binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(note: Note) {
            //17.3 Заполнение данными(название, цена, количество)
            binding.apply {
                noteHeading.text = note.heading
                noteDescription.text = note.description
                noteColor.setBackgroundColor(note.color.toInt())
            }
        }
    }

    //17.4 Переопределение метода для адаптера
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        //17.5 Раздувание макета из item_note.xml
        return NoteViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context)))
    }

    //17.6 Переопределение метода для адаптера
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        //17.7 Получение текущего элемента с помощью getItem()
        val current = getItem(position)
        //17.8 Установка прослушивателя кликов
        holder.itemView.setOnClickListener {
            onNoteClicked(current)
        }
        holder.bind(current)
    }
    //17.9 Создание объекта для сравнения с предыдущими версиями(шаблонный код)
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.heading == newItem.heading && oldItem.description == newItem.description && oldItem.color == newItem.color
            }
        }
    }
}