package com.devkazonovic.projects.mytasks.presentation.task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.databinding.TaskListsMenuFragmentBinding
import com.devkazonovic.projects.mytasks.presentation.task.TaskViewModel
import com.devkazonovic.projects.mytasks.presentation.task.adapter.TaskListsMenuAdapter
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.ListsDiffCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TaskListsMenuFragment : BottomSheetDialogFragment() {

    private var _binding: TaskListsMenuFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskListsMenuAdapter

    private val viewModel by viewModels<TaskViewModel>(
        { requireParentFragment() }
    )

    companion object {
        fun newInstance() = TaskListsMenuFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TaskListsMenuFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.taskList.observe(viewLifecycleOwner, { list ->
            Timber.d("$list")
            viewModel.getLists()
            viewModel.lists.observe(viewLifecycleOwner, { lists ->
                initRecyclerView(list.id)
                adapter.submitList(lists)
            })
        })
    }

    private fun initRecyclerView(longID: Long) {
        binding.recyclerViewTasksLists.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskListsMenuAdapter(longID, ListsDiffCallback()) { taskList ->
            viewModel.updateCurrentTaskList(taskList.id)
        }
        binding.recyclerViewTasksLists.adapter = adapter
    }

}