package com.devkazonovic.projects.mytasks.presentation.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.MyTasksApplication
import com.devkazonovic.projects.mytasks.data.TasksRepositoryImpl
import com.devkazonovic.projects.mytasks.databinding.TaskListsMenuFragmentBinding
import com.devkazonovic.projects.mytasks.domain.MySharedPreferences
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksListsDiffCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

class TaskListsMenuFragment : BottomSheetDialogFragment() {

    private var _binding: TaskListsMenuFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskListsMenuAdapter

    private val viewModel: TaskViewModel by viewModels(
        { requireParentFragment() },
        {
            TaskViewModelFactory(
                TasksRepositoryImpl((requireActivity().application as MyTasksApplication).dao)
            )
        }
    )

    private lateinit var mySharedPreferences: MySharedPreferences

    companion object {
        fun newInstance() = TaskListsMenuFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TaskListsMenuFragmentBinding.inflate(layoutInflater)
        mySharedPreferences = MySharedPreferences(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskList.observe(viewLifecycleOwner, { list ->
            Timber.d("$list")
            viewModel.getTasksLists()
            viewModel.tasksLists.observe(viewLifecycleOwner, { lists ->
                initRecyclerView(list.id)
                adapter.submitList(lists)
            })
        })
    }

    private fun initRecyclerView(longID: Long) {
        binding.recyclerViewTasksLists.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskListsMenuAdapter(longID, TasksListsDiffCallback()) { taskList ->
            viewModel.updateCurrentTaskList(taskList.id)
        }

        binding.recyclerViewTasksLists.adapter = adapter
    }

}