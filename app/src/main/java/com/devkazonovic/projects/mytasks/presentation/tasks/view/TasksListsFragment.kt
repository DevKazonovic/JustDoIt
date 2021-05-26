package com.devkazonovic.projects.mytasks.presentation.tasks.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.MyTasksApplication
import com.devkazonovic.projects.mytasks.data.TasksRepositoryImpl
import com.devkazonovic.projects.mytasks.databinding.AddNewlistFragmentBinding
import com.devkazonovic.projects.mytasks.databinding.ListsFragmentBinding
import com.devkazonovic.projects.mytasks.domain.MySharedPreferences
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModelFactory
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksListsAdapter
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksListsDiffCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TasksListsFragment : BottomSheetDialogFragment() {

    private var _binding: ListsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TasksListsAdapter

    private val viewModel: TasksViewModel by viewModels(
        { requireParentFragment() },
        {
            TasksViewModelFactory(
                TasksRepositoryImpl((requireActivity().application as MyTasksApplication).dao)
            )
        }
    )

    private lateinit var mySharedPreferences: MySharedPreferences

    companion object {
        fun newInstance() = TasksListsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListsFragmentBinding.inflate(layoutInflater)
        mySharedPreferences = MySharedPreferences(requireContext())
        initRecyclerView()
        binding.cardViewAddNewList.setOnClickListener {
            createNewList()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLists()
        viewModel.tasksLists.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    private fun initRecyclerView() {
        binding.recyclerViewTasksLists.layoutManager = LinearLayoutManager(requireContext())
        adapter = TasksListsAdapter(TasksListsDiffCallback()) {
            if (mySharedPreferences.saveCurrentTasksList(it.id)) {
                viewModel.updateCurrentList(it.id)
                dismiss()
            }
        }

        binding.recyclerViewTasksLists.adapter = adapter
    }

    private fun createNewList() {
        val view = AddNewlistFragmentBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("List Name")
        }

        builder.setView(view.root)
        builder.apply {
            setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
            setPositiveButton("Create") { dialog, which ->
                viewModel.createNewList(view.editTextListName.text.toString())
            }
        }.show()
    }
}