package com.egorpoprotskiy.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.egorpoprotskiy.note.ViewModel.NoteViewModel
import com.egorpoprotskiy.note.ViewModel.NoteViewModelFactory
import com.egorpoprotskiy.note.adapter.NoteListAdapter
import com.egorpoprotskiy.note.databinding.FragmentNoteListBinding

/**
 * A simple [Fragment] subclass.
 * Use the [NoteListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoteListFragment : Fragment() {

    //19.1 Использовать by activityViewModels() делегат свойства Kotlin для совместного использования ViewModel по фрагментам
    private val viewModel: NoteViewModel by activityViewModels{
        // 19.2 вызовите NoteViewModelFactory() конструктор и передать в NoteDao экземпляр. Использовать database экземпляр, созданный вами в одной из предыдущих задач, для вызова noteDao конструктор.
        NoteViewModelFactory((activity?.application as NoteApplication).database.noteDao())
    }
    //7.1 Объявление binding
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // 7.2 Раздувание макета через binding
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //19.3 объявить val названный adapter. Инициализировать новый adapter свойство с помощью конструктора по умолчанию, NoteListAdapter{} переходя в ничто(т.е.сначала сделать его пустым).
        val adapter = NoteListAdapter {
            //19.4 добавить переход на фрагмент с деталями одного продукта. Далее в InventoryViewModel.
            val action = NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        //19.5 Привязать только что созданный adapter к recyclerView cледующим образом
        binding.recyclerView.adapter = adapter
        //19.6 Прикрепите наблюдателя на allItems для прослушивания изменений данных..
        // ..Внутри наблюдателя вызовите submitList() на adapter и перейти в новый список. Это обновит RecyclerView новыми элементами в списке.
        viewModel.allItems.observe(this.viewLifecycleOwner) { items -> items.let { adapter.submitList(it) }}

        // 7.3 RecyclerView по-умолчанию будет списком
//        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 2)

        // 7.4 Переход на на другой фрагмент
        binding.addNote.setOnClickListener {
            val action = NoteListFragmentDirections.actionNoteListFragmentToNoteAddFragment(
                // 7.5 передача в другой фрагмент заголовка.(Чтобы добавить этот ресурс надо в navGraph добавить аргумент(label)
                getString(R.string.add_fragment_note)
            )
            this.findNavController().navigate(action)
        }
    }
}