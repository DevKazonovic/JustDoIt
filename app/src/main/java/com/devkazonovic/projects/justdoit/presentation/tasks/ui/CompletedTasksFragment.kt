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
import com.devkazonovic.projects.justdoit.databinding.FragmentTasksCompletedBinding
import com.devkazonovic.projects.justdoit.domain.model.Task
import com.devkazonovic.projects.justdoit.help.extension.hide
import com.devkazonovic.projects.justdoit.help.extension.show
import com.devkazonovic.projects.justdoit.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.CompletedTasksAdapter
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.TasksDiffCallback
import com.devkazonovic.projects.justdoit.service.DateTimeHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val KEY_TASK_ID = "Task ID"

@AndroidEntryPoint
class CompletedTasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel>({ requireParentFragment() })

    private var _binding: FragmentTasksCompletedBinding? = null
    private val binding get() = _binding!!

    private lateinit var completedTasksAdapter: CompletedTasksAdapter

    @Inject
    lateinit var dateTimeHelper: DateTimeHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTasksCompletedBinding.inflate(inflater)
        setUpRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.completedTasks.observe(viewLifecycleOwner) {
            showCompletedTasks(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        completedTasksAdapter = CompletedTasksAdapter(
            TasksDiffCallback(),
            { viewModel.markTaskAsCompleted(it.id, false) },
            {
                findNavController()
                    .navigate(R.id.action_tasks_to_taskDetail, bundleOf(KEY_TASK_ID to it))
            },
            dateTimeHelper
        )
        binding.recyclerViewTasks.adapter = completedTasksAdapter
    }

    private fun showCompletedTasks(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            binding.recyclerViewTasks.hide()
            binding.layoutEmptyPage.root.show()
            binding.layoutEmptyPage.textViewState.text = getString(R.string.label_just_doit)
        } else {
            binding.layoutEmptyPage.root.hide()
            binding.recyclerViewTasks.show()
            completedTasksAdapter.submitList(tasks)

        }
    }

}