package com.egorpoprotskiy.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.egorpoprotskiy.note.ViewModel.NoteViewModel
import com.egorpoprotskiy.note.ViewModel.NoteViewModelFactory
import com.egorpoprotskiy.note.databinding.FragmentNoteDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.egorpoprotskiy.note.model.Note


/**
 * A simple [Fragment] subclass.
 * Use the [NoteDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoteDetailFragment : Fragment() {
    //9.1 Объявление переменной navArgs(). Аргументы должны присутствовать в nav_graph этих фрагментов
    private val navigationArgs: NoteDetailFragmentArgs by navArgs()

    //21.1 Привязка данных к TextView
    lateinit var note: Note
    //21.2 Использовать by activityViewModels() делегат свойства Kotlin для совместного использования ViewModel по фрагментам
    private val viewModel: NoteViewModel by activityViewModels{
        // 21.3 вызовите InventoryViewModelFactory() конструктор и передать в ItemDao экземпляр. Использовать database экземпляр, созданный вами в одной из предыдущих задач, для вызова itemDao конструктор.
        NoteViewModelFactory((activity?.application as NoteApplication).database.noteDao())
    }
    //21.4 создать private функция называется bind() который принимает экземпляр Note entity в качестве параметра и ничего не возвращает.
    private fun bind(note: Note) {
        binding.apply {
            //21.5 Заполнение данными(название, описание)
            noteHeading.text = note.heading
            noteDescription.text = note.description
            noteColor.setBackgroundColor(note.color.toInt())
            //23.2 Добавить слушатель нажатий на кнопку удаления товара.(на Этом пункте заканчивается Удаление объетов из БД)
            deleteButton.setOnClickListener { showConfirmationDialog() }
            //24.3 Добавить слушатель нажатий на кнопку редактирования товара
            editButton.setOnClickListener { editNote() }
        }
    }

    //21.6 Создать onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //21.7 под вызовом функции суперкласса создайте неизменяемую переменную с именем id. Получите и назначьте аргумент навигации этой новой переменной.
        val id = navigationArgs.itemId
        viewModel.retrieveNote(id).observe(this.viewLifecycleOwner) { selectedNote ->
            note = selectedNote
            bind(note)
        }
    }

    // 9.2 Объявление binding
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // 9.3 Раздувание макета через binding
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    //9.4 Отображает диалоговое окно предупреждения для получения подтверждения пользователя перед удалением элемента.(Данная функция пригодится позже)
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) {_, _ -> }
            .setPositiveButton(getString(R.string.yes)) {_, _ ->
                //9.5 Вызов функции по удалению элемента
                deleteNote()
            }
            .show()
    }
    //9.6 Функция удаления элемента
    private fun deleteNote() {
        //23.1 Удаляет текущий элемент
        viewModel.deleteNote(note)
        //9.7 переход к фрагменту списка
        findNavController().navigateUp()
    }

    //24.1 РЕДАКТИРОВАНИЕ ЭЛЕМЕНТОВ - НАЧАЛО
    private fun editNote() {
        //24.2 Переход на экран редактирования и изменения заголовка экрана.
        val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToNoteAddFragment(getString(R.string.edit_fragment_note), note.id)
        this.findNavController().navigate(action)
    }

    //9.8 Вызывается, когда фрагмент уничтожен
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

