package com.egorpoprotskiy.note


import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.egorpoprotskiy.note.ViewModel.NoteViewModel
import com.egorpoprotskiy.note.ViewModel.NoteViewModelFactory
import com.egorpoprotskiy.note.databinding.FragmentNoteAddBinding
import com.egorpoprotskiy.note.model.Note


/**
 * A simple [Fragment] subclass.
 * Use the [NoteAddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class NoteAddFragment : Fragment() {

    //8.1 Объявление переменной navArgs(). Аргументы должны присутствовать в navGraph этих фрагментов
    private val navigationArgs: NoteDetailFragmentArgs by navArgs()

    // 8.2 Объявление binding
    private var _binding: FragmentNoteAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // 8.3 Раздувание макета через binding
        _binding = FragmentNoteAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 8.4 Скрыть клавиатуру
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    /*Совет: в основном это шаблонный код, поэтому вы можете повторно использовать код в будущем для создания экземпляра ViewModel с помощью фабрики ViewModel.*/

    // 16.1 Использовать by activityViewModels() делегат свойства Kotlin для совместного использования ViewModel по фрагментам
    private val viewModel: NoteViewModel by activityViewModels {
        // 16.2 вызовите NoteViewModelFactory() конструктор и передать в noteDao экземпляр. Использовать database экземпляр, созданный вами в одной из предыдущих задач, для вызова noteDao конструктор.
        NoteViewModelFactory((activity?.application as NoteApplication).database.noteDao())
    }
    //16.3 Ниже viewModel определение, создать lateinit var называется note типа Note.
    lateinit var note: Note

    // 16.4 Данная функция возвращяет НЕ пустые строки.
    private fun isEntryValid(): Boolean {
        // 16.5 Вызов функции из NoteViewModel
        return viewModel.isEntryValid(
            binding.noteHeading.text.toString(),
            binding.noteDescription.text.toString(),
            binding.noteColor.checkedRadioButtonId.toString()
        )
    }
    // 16.6 Принимает 3 введённые строки
    private fun addNewNote() {
        // 16.7 Вызов функции из NoteViewModel. Проверка, чтобы строки не были пустыми
        if (isEntryValid()) {
            // 16.8 Вызов функции из NoteViewModel
            viewModel.addNewNote(
                binding.noteHeading.text.toString(),
                binding.noteDescription.text.toString(),
                when (binding.noteColor.checkedRadioButtonId) {
                    R.id.orange -> resources.getColor(R.color.orange).toString()
                    R.id.blue -> resources.getColor(R.color.blue).toString()
                    R.id.green -> resources.getColor(R.color.green).toString()
                    R.id.pink -> resources.getColor(R.color.pink).toString()
                    else -> resources.getColor(R.color.defaultColor).toString()
                }
            )
        }
        //16.9 Навигация для возврата в NoteListFragment
        val action = NoteAddFragmentDirections.actionNoteAddFragmentToNoteListFragment()
        findNavController().navigateUp()
    }

    //16.10 Создание слушателя нажатий для кнопки "сохранить". На данном пункте функционал без блока "if", только то, что после else (На этом пункте заканчивается работа по созданию БД)
    //Далее идет создание адаптера для recyclerview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //25.3 получение данных от аргумента навигации
        val id = navigationArgs.itemId
        //25.4 Отслеживание изменений после редактирования (Далее в NoteViewModel)
        if (id > 0) {
            viewModel.retrieveNote(id).observe(this.viewLifecycleOwner) { selectedNote ->
                note = selectedNote
                bind(note)
            }
        } else {
            binding.saveButton.setOnClickListener {
                addNewNote()
            }
        }
    }

    //25.1 Добавление новой функции для редактирования объектов
    private fun bind(note: Note) {
        //25.2 Редактирование объектов
        binding.apply {
            noteHeading.setText(note.heading, TextView.BufferType.SPANNABLE)
            noteDescription.setText(note.description, TextView.BufferType.SPANNABLE)
            when (noteColor.checkedRadioButtonId) {
                R.id.orange -> resources.getColor(R.color.orange).toString()
                R.id.blue -> resources.getColor(R.color.blue).toString()
                R.id.green -> resources.getColor(R.color.green).toString()
                R.id.pink -> resources.getColor(R.color.pink).toString()
                else -> resources.getColor(R.color.defaultColor).toString()
            }
            //27.2 Слушатель нажатий на кнопку сохранить, но уже изменённые данные
            saveButton.setOnClickListener { updateNote() }
        }
    }

    //27.1 Проверка, что все поля заполнены(если заполнены, то переход в ItemListFragment)
    private fun updateNote() {
        if (isEntryValid()) {
            viewModel.updateNote(
                this.navigationArgs.itemId,
                this.binding.noteHeading.text.toString(),
                this.binding.noteDescription.text.toString(),
                when (binding.noteColor.checkedRadioButtonId) {
                    R.id.orange -> resources.getColor(R.color.orange).toString()
                    R.id.blue -> resources.getColor(R.color.blue).toString()
                    R.id.green -> resources.getColor(R.color.green).toString()
                    R.id.pink -> resources.getColor(R.color.pink).toString()
                    else -> resources.getColor(R.color.defaultColor).toString()
                }
            )
            val action = NoteAddFragmentDirections.actionNoteAddFragmentToNoteListFragment()
            findNavController().navigate(action)
        }
    }
}


