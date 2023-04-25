package com.egorpoprotskiy.note

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egorpoprotskiy.note.ViewModel.NoteViewModel
import com.egorpoprotskiy.note.ViewModel.NoteViewModelFactory
import com.egorpoprotskiy.note.adapter.NoteListAdapter
import com.egorpoprotskiy.note.databinding.FragmentNoteListBinding
import com.egorpoprotskiy.note.model.Note
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 * Use the [NoteListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoteListFragment : Fragment() {

    //19.1 Использовать by activityViewModels() делегат свойства Kotlin для совместного использования ViewModel по фрагментам
    private val viewModel: NoteViewModel by activityViewModels {
        // 19.2 вызовите NoteViewModelFactory() конструктор и передать в NoteDao экземпляр. Использовать database экземпляр, созданный вами в одной из предыдущих задач, для вызова noteDao конструктор.
        NoteViewModelFactory((activity?.application as NoteApplication).database.noteDao())
    }

    //7.1 Объявление binding
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteListAdapter
    lateinit var note: Note

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
        noteAdapter = NoteListAdapter {
            //19.4 добавить переход на фрагмент с деталями одного продукта. Далее в InventoryViewModel.
            val action =
                NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        //19.5 Привязать только что созданный adapter к recyclerView cледующим образом
        binding.recyclerView.adapter = noteAdapter
        //19.6 Прикрепите наблюдателя на allItems для прослушивания изменений данных..
        // ..Внутри наблюдателя вызовите submitList() на adapter и перейти в новый список. Это обновит RecyclerView новыми элементами в списке.
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                noteAdapter.submitList(
                    it
                )
            }
        }
        //Вызов функции свайпа
        deleteItemSwipe(binding.recyclerView)
        editItemSwipe(binding.recyclerView)

        // 7.3 RecyclerView по-умолчанию будет списком
//        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
//        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 2)


        // 7.4 Переход на на другой фрагмент
        binding.addNote.setOnClickListener {
            val action = NoteListFragmentDirections.actionNoteListFragmentToNoteAddFragment(
                // 7.5 передача в другой фрагмент заголовка.(Чтобы добавить этот ресурс надо в navGraph добавить аргумент(label)
                getString(R.string.add_fragment_note)
            )
            this.findNavController().navigate(action)
        }
    }

    //Удаление заметки с помощью свайпа
    private fun deleteItemSwipe(recyclerViewNote: RecyclerView) {
        val callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = noteAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteNote(item)
            }

//            val trashIcon = resources.getDrawable(R.drawable.baseline_delete_outline_big, null)

            //---------------------
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                c.clipRect(
                    0f,
                    viewHolder.itemView.top.toFloat(),
                    dX,
                    viewHolder.itemView.bottom.toFloat()
                )
//                c.drawColor(Color.GREEN)

                val trashIcon = resources.getDrawable(R.drawable.baseline_delete_outline_big, null)
                val textMargin = resources.getDimension(R.dimen.marginAll3).roundToInt()
                trashIcon.bounds = Rect(
                    textMargin,
                    viewHolder.itemView.top + textMargin,
                    textMargin + trashIcon.intrinsicWidth,
                    viewHolder.itemView.top + trashIcon.intrinsicHeight + textMargin
                )
                trashIcon.draw(c)

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
//--------------------
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerViewNote)
    }

    //Редактирование заметки с помощью свайпа
    private fun editItemSwipe(recyclerViewNote: RecyclerView) {
        val callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = noteAdapter.currentList[viewHolder.adapterPosition]
                bind(item)
            }
            //---------------------
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                c.clipRect(
                    viewHolder.itemView.right.toFloat() + dX,
                    viewHolder.itemView.top.toFloat(),
                    viewHolder.itemView.right.toFloat(),
                    viewHolder.itemView.bottom.toFloat()
                )
//                c.drawColor(Color.GREEN)

                val editIcon = resources.getDrawable(R.drawable.baseline_edit_orange, null)
                val textMargin = resources.getDimension(R.dimen.marginAll3).roundToInt()
                editIcon.bounds = Rect(
                    viewHolder.itemView.right - editIcon.intrinsicWidth - textMargin,
                    viewHolder.itemView.top + textMargin,
                    viewHolder.itemView.right - textMargin,
                    viewHolder.itemView.top + editIcon.intrinsicHeight + textMargin
                )
                editIcon.draw(c)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
//--------------------
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerViewNote)
    }

    private fun bind(note: Note) {
        val action = NoteListFragmentDirections.actionNoteListFragmentToNoteAddFragment(
            getString(R.string.edit_fragment_note),
            note.id
        )
        this.findNavController().navigate(action)
    }

}