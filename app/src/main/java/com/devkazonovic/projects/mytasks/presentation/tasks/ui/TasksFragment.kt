package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.TasksFragmentBinding
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TaskTouchHelper
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksAdapter
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksDiffCallback
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val KEY_TASK_ID = "Task ID"

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var completedTasksAdapter: TasksAdapter
    private var _binding: TasksFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TasksViewModel>()
    private var showCompletedTasks = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TasksFragmentBinding.inflate(inflater)
        initRecyclerView()
        binding.fab.setOnClickListener {
            AddNewTaskFragment.newInstance().show(childFragmentManager, "add_task_fragment")
        }
        binding.viewShowHideCompletedTasks.setOnClickListener {
            binding.recyclerViewCompletedTasks.isVisible = showCompletedTasks
            showCompletedTasks = if (showCompletedTasks) {
                binding.imageViewDropDownIcon.setImageResource(R.drawable.ic_round_arrow_drop_up)
                false
            } else {
                binding.imageViewDropDownIcon.setImageResource(R.drawable.ic_round_arrow_drop_down)
                true
            }
        }
        actionBarListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")

        viewModel.currentTaskList.observe(viewLifecycleOwner, {
            it?.let {
                binding.textViewTasksList.text = it.name
                viewModel.updateTasks()
            }
        })
        viewModel.tasks.observe(viewLifecycleOwner, {
            it?.let {
                showTasks(it)
            }

        })
        viewModel.completedTasks.observe(viewLifecycleOwner, {
            it?.let {
                showCompletedTasks(it)
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initRecyclerView() {
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        tasksAdapter = TasksAdapter(
            TasksDiffCallback(),
            { viewModel.markTaskAsCompleted(it.id, true) },
            {
                findNavController()
                    .navigate(R.id.action_tasks_to_taskDetail, bundleOf(KEY_TASK_ID to it))
            }
        )
        binding.recyclerViewTasks.adapter = tasksAdapter

        val taskTouchHelper = ItemTouchHelper(TaskTouchHelper(tasksAdapter))
        taskTouchHelper.attachToRecyclerView(binding.recyclerViewTasks)

        binding.recyclerViewCompletedTasks.layoutManager = LinearLayoutManager(requireContext())
        completedTasksAdapter = TasksAdapter(
            TasksDiffCallback(),
            { viewModel.markTaskAsCompleted(it.id, false) },
            {
                findNavController()
                    .navigate(R.id.action_tasks_to_taskDetail, bundleOf(KEY_TASK_ID to it))
            }
        )
        binding.recyclerViewCompletedTasks.adapter = completedTasksAdapter

    }

    private fun showTasks(tasks: List<Task>) {
        tasksAdapter.submitList(tasks)
    }

    private fun showCompletedTasks(tasks: List<Task>) {
        binding.layoutCompletedTasks.isVisible = tasks.isNotEmpty()
        binding.textViewCompletedTasks.text = "Completed (${tasks.size})"
        completedTasksAdapter.submitList(tasks)
    }

    private fun actionBarListeners() {
        binding.bottomAppBar.setNavigationOnClickListener {
            val fragment = TasksListsFragment.newInstance()
            fragment.show(childFragmentManager, "tasks_lists_fragment")
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.action_show_menu -> {
                    TasksMenuFragment.newInstance()
                        .show(childFragmentManager, "tasks_menu_fragment")
                    true
                }
                else -> false
            }
        }
    }


}