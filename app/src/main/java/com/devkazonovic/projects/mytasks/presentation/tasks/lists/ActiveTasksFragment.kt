package com.devkazonovic.projects.mytasks.presentation.tasks.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentTasksActiveBinding
import com.devkazonovic.projects.mytasks.help.extension.hide
import com.devkazonovic.projects.mytasks.help.extension.show
import com.devkazonovic.projects.mytasks.presentation.common.model.SortDirection
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.ActiveTasksAdapter
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.diff.ActiveTasksDiffCallback
import com.devkazonovic.projects.mytasks.presentation.tasks.model.ActiveTask
import com.devkazonovic.projects.mytasks.presentation.tasks.model.TasksSort
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val KEY_TASK_ID = "Task ID"

@AndroidEntryPoint
class ActiveTasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel>({ requireParentFragment() })

    private var _binding: FragmentTasksActiveBinding? = null
    private val binding get() = _binding!!

    private lateinit var tasksAdapter: ActiveTasksAdapter

    @Inject
    lateinit var dateTimeHelper: DateTimeHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTasksActiveBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpListeners()
        viewModel.activeTasks.observe(viewLifecycleOwner) {
            showActiveTasks(it)
        }
        viewModel.sort.observe(viewLifecycleOwner) {
            binding.viewSort.text = when (it) {
                TasksSort.DEFAULT -> getString(R.string.label_sort_default)
                TasksSort.DATE -> getString(R.string.label_sort_date)
                TasksSort.NAME -> getString(R.string.label_sort_name)
            }
        }

        viewModel.order.observe(viewLifecycleOwner) {
            when (it) {
                SortDirection.ASC -> {
                    binding.viewSortDirection.setImageResource(R.drawable.ic_arrow_up)
                }
                SortDirection.DESC -> {
                    binding.viewSortDirection.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpListeners() {
        binding.viewSort.setOnClickListener {
            val enums = TasksSort.values()
            val enumsNames = enums.map {
                when (it) {
                    TasksSort.DEFAULT -> getString(R.string.label_sort_default)
                    TasksSort.DATE -> getString(R.string.label_sort_date)
                    TasksSort.NAME -> getString(R.string.label_sort_name)
                }
            }.toTypedArray()
            val checkedItem = enums.indexOf(viewModel.sort.value)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.label_sort_title))
                .setSingleChoiceItems(enumsNames, checkedItem) { dialog, which ->
                    viewModel.setSort(enums[which])
                }
                .show()
        }
        binding.viewSortDirection.setOnClickListener {
            viewModel.switchOrder()
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        tasksAdapter = ActiveTasksAdapter(
            ActiveTasksDiffCallback(),
            { viewModel.markTaskAsCompleted(it.id, true) },
            {
                findNavController()
                    .navigate(R.id.action_tasks_to_taskDetail, bundleOf(KEY_TASK_ID to it))
            },
            dateTimeHelper
        )
        binding.recyclerViewTasks.adapter = tasksAdapter
    }

    private fun showActiveTasks(tasks: List<ActiveTask>) {
        if (tasks.isEmpty()) {
            binding.recyclerViewTasks.hide()
            binding.textViewState.show()
            binding.textViewState.text = getString(R.string.emptyList)
        } else {
            binding.textViewState.hide()
            binding.recyclerViewTasks.show()
            tasksAdapter.submitList(tasks)

        }
    }

}