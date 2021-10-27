package com.devkazonovic.projects.justdoit.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.FragmentTasksActiveBinding
import com.devkazonovic.projects.justdoit.help.extension.hide
import com.devkazonovic.projects.justdoit.help.extension.show
import com.devkazonovic.projects.justdoit.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.ActiveTasksAdapter
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.ActiveTasksDiffCallback
import com.devkazonovic.projects.justdoit.presentation.tasks.model.ActiveTask
import com.devkazonovic.projects.justdoit.service.DateTimeHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val KEY_TASK_ID = "Task ID"
private const val KEY_SELECTED_TASK_ID = "SelectedTask ID"


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
        viewModel.activeTasks.observe(viewLifecycleOwner) {
            showActiveTasks(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        tasksAdapter = ActiveTasksAdapter(
            ActiveTasksDiffCallback(),
            dateTimeHelper,
            { viewModel.markTaskAsCompleted(it.id, true) },
            { view, id ->
                onItemClick(view, id)
            }, { view, id ->
                onLongItemClick(view, id)
            },
            viewModel,
            false
        )
        binding.recyclerViewTasks.adapter = tasksAdapter
    }

    private fun showActiveTasks(tasks: List<ActiveTask>) {
        if (tasks.isEmpty()) {
            binding.recyclerViewTasks.hide()
            binding.layoutEmptyPage.root.show()
            binding.layoutEmptyPage.textViewState.text = getString(R.string.label_empty_page)
        } else {
            binding.layoutEmptyPage.root.hide()
            binding.recyclerViewTasks.show()
            tasksAdapter.submitList(tasks)

        }
    }

    private fun onItemClick(view: View, taskId: Long) {
        findNavController()
            .navigate(R.id.action_tasks_to_taskDetail, bundleOf(KEY_TASK_ID to taskId))
    }

    private fun onLongItemClick(view: View, taskId: Long) {
        findNavController().navigate(R.id.action_tasks_to_tasksSelection,
            bundleOf(
                KEY_SELECTED_TASK_ID to taskId
            ))
    }
}