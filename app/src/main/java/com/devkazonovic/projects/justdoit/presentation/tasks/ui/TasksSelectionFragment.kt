package com.devkazonovic.projects.justdoit.presentation.tasks.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.FragmentTasksSelectionBinding
import com.devkazonovic.projects.justdoit.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.ActiveTasksAdapter
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.ActiveTasksDiffCallback
import com.devkazonovic.projects.justdoit.presentation.tasks.model.ActiveTask
import com.devkazonovic.projects.justdoit.presentation.tasks.util.selectTaskCard
import com.devkazonovic.projects.justdoit.presentation.tasks.util.unSelectTaskCard
import com.devkazonovic.projects.justdoit.service.DateTimeHelper
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val KEY_SELECTED_TASK_ID = "SelectedTask ID"
private var taskId: Long? = null

@AndroidEntryPoint
class TasksSelectionFragment : Fragment() {

    @Inject
    lateinit var dateTimeHelper: DateTimeHelper

    @Inject
    lateinit var navController: NavController

    @Inject
    lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel by viewModels<TasksViewModel>()

    private var _binding: FragmentTasksSelectionBinding? = null
    private val binding get() = _binding!!

    private var actionMode: ActionMode? = null

    private lateinit var tasksAdapter: ActiveTasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getLong(KEY_SELECTED_TASK_ID, -1L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTasksSelectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.start()

        setUpRecyclerView()

        viewModel.activeTasks.observe(viewLifecycleOwner) { list ->
            list?.let {
                showActiveTasks(it)
            }
        }

        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { navigate ->
                if (navigate) {
                    removeActionMode()
                }
            }
        }

        viewModel.selectedTasks.observe(viewLifecycleOwner) { list ->
            list?.let {
                if (it.isEmpty()) removeActionMode()
                else actionMode?.title = "${it.size}"
            }
        }
        taskId?.let {
            viewModel.addItem(it)
        }
        viewModel.selectedTasks.value?.let {
            if (it.isNotEmpty()) {
                setActionMode()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        binding.recyclerViewSelectedTasks.layoutManager = LinearLayoutManager(requireContext())
        tasksAdapter = ActiveTasksAdapter(
            ActiveTasksDiffCallback(),
            dateTimeHelper,
            { viewModel.markTaskAsCompleted(it.id, true) },
            { view, id -> onItemClick(view, id) },
            { view, id -> onLongItemClick(view, id) },
            viewModel,
            true
        )
        binding.recyclerViewSelectedTasks.adapter = tasksAdapter
    }

    private fun showActiveTasks(tasks: List<ActiveTask>) {
        tasksAdapter.submitList(tasks)
    }

    private fun onItemClick(view: View, taskId: Long) {
        viewModel.selectedTasks.value?.let { selectedTasks ->
            if (selectedTasks.isNotEmpty()) {
                if (selectedTasks.contains(taskId)) {
                    (view as MaterialCardView).unSelectTaskCard(requireContext())
                    viewModel.removeItem(taskId)
                } else {
                    (view as MaterialCardView).selectTaskCard(requireContext())
                    viewModel.addItem(taskId)
                }
            }
        }
    }

    private fun onLongItemClick(view: View, taskId: Long) {
        viewModel.selectedTasks.value?.let { selectedTasks ->
            if (selectedTasks.isEmpty()) {
                actionMode = activity?.startActionMode(actionMode())
            }
            if (selectedTasks.contains(taskId)) {
                (view as MaterialCardView).unSelectTaskCard(requireContext())
                viewModel.removeItem(taskId)
            } else {
                (view as MaterialCardView).selectTaskCard(requireContext())
                viewModel.addItem(taskId)
            }
        }
    }

    private fun actionMode(): ActionMode.Callback {
        return object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                requireActivity().menuInflater.inflate(R.menu.menu_tasks_selection, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.deleteAll -> {
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setMessage(getString(R.string.label_confirmation_title))
                            setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }
                            setPositiveButton(getString(R.string.label_yes)) { _, _ ->
                                viewModel.deleteSelectedTasks()
                            }
                        }.show()

                        true
                    }

                    R.id.moveTo -> {
                        MoveToCategoryFragment.newInstance()
                            .show(childFragmentManager, MoveToCategoryFragment.TAG)
                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                viewModel.clearSelectedItems()
                actionMode = null
                findNavController().navigateUp()
            }
        }
    }

    private fun setActionMode() {
        actionMode = requireActivity().startActionMode(actionMode())
    }

    private fun removeActionMode() {
        actionMode?.finish()
    }
}