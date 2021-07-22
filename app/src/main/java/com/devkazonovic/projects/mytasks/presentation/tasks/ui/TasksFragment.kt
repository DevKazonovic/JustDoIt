package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.TasksFragmentBinding
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.extension.hide
import com.devkazonovic.projects.mytasks.help.extension.setupRefreshLayout
import com.devkazonovic.projects.mytasks.help.extension.setupSnackBar
import com.devkazonovic.projects.mytasks.help.extension.show
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TaskTouchHelper
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksAdapter
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksDiffCallback
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

private const val KEY_TASK_ID = "Task ID"

@AndroidEntryPoint
class TasksFragment : Fragment() {

    @Inject
    lateinit var dateTimeHelper: DateTimeHelper
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
        addListeners()
        setupRefreshLayout(binding.refreshLayout, binding.nestedScrollViewContainer)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        observeLoadingState()
        observeData()
        observeErrors()
        observeUserNotice()
    }

    private fun initRecyclerView() {
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        tasksAdapter = TasksAdapter(
            TasksDiffCallback(),
            { viewModel.markTaskAsCompleted(it.id, true) },
            {
                findNavController()
                    .navigate(R.id.action_tasks_to_taskDetail, bundleOf(KEY_TASK_ID to it))
            },
            dateTimeHelper
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
            },
            dateTimeHelper
        )
        binding.recyclerViewCompletedTasks.adapter = completedTasksAdapter

    }

    private fun addListeners() {
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
        binding.refreshLayout.setOnRefreshListener {
            viewModel.updateCurrentCategory()
        }
    }

    private fun observeLoadingState() {
        viewModel.isDownloading.observeWithViewLifecycleOwner {
            showProgressBar(it)
        }
    }

    private fun observeData() {
        viewModel.currentCategory.observeWithViewLifecycleOwner {
            onCurrentCategoryChange(it)
        }
        viewModel.unCompletedTasks.observeWithViewLifecycleOwner {
            showActiveTasks(it)

        }
        viewModel.completedTasks.observeWithViewLifecycleOwner {
            showCompletedTasks(it)
        }
    }

    private fun observeUserNotice() {
        binding.fab.setupSnackBar(
            viewLifecycleOwner,
            viewModel.snackBarEvent,
            Snackbar.LENGTH_SHORT
        )
    }

    private fun observeErrors() {
        viewModel.mainViewErrorEvent.observeWithViewLifecycleOwner {
            it.getContentIfNotHandled()?.let { error ->
                onError(error) {

                }
            }
        }
        viewModel.userInputErrorEvent.observeWithViewLifecycleOwner {

        }
        binding.fab.setupSnackBar(
            viewLifecycleOwner,
            viewModel.snackBarErrorEvent,
            Snackbar.LENGTH_SHORT
        )

    }

    private fun onError(message: Int, action: () -> Unit) {
        binding.viewData.hide()
        binding.viewError.root.show()
        binding.viewError.buttonActionOnError.setOnClickListener {
            action()
        }
        binding.viewError.textViewErrorMessage.text = getString(message)
    }

    private fun onCurrentCategoryChange(newCategory: Category) {
        newCategory.let {
            binding.textViewTasksList.text = it.name
            binding.viewError.root.hide()
            binding.viewData.show()
            viewModel.observeTasks()
        }
    }

    private fun showProgressBar(isDownloading: Boolean) {
        binding.refreshLayout.isRefreshing = isDownloading
    }

    private fun showActiveTasks(tasks: List<Task>) {
        tasksAdapter.submitList(tasks)
    }

    private fun showCompletedTasks(tasks: List<Task>) {
        binding.layoutCompletedTasks.isVisible = tasks.isNotEmpty()
        binding.textViewCompletedTasks.text = "Completed (${tasks.size})"
        completedTasksAdapter.submitList(tasks)
    }


    private fun <T> LiveData<T>.observeWithViewLifecycleOwner(onChange: (T) -> Unit) {
        this.observe(viewLifecycleOwner, {
            onChange(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}