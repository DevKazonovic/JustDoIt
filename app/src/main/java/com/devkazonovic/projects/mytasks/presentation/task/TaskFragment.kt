package com.devkazonovic.projects.mytasks.presentation.task

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.devkazonovic.projects.mytasks.MyTasksApplication
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.data.TasksRepositoryImpl
import com.devkazonovic.projects.mytasks.databinding.TaskFragmentBinding
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList

private const val KEY_TASK_ID = "Task ID"

class TaskFragment : Fragment() {

    private var taskID: Long? = null
    private var listID: Long? = null

    private var _binding: TaskFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            TasksRepositoryImpl(
                (requireActivity().application as MyTasksApplication).dao
            )
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskID = it.getLong(KEY_TASK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TaskFragmentBinding.inflate(layoutInflater)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)

        binding.topAppBar.setOnMenuItemClickListener { menuitem ->
            when (menuitem.itemId) {
                R.id.action_delete -> {
                    viewModel.deleteTask()
                    findNavController().navigateUp()
                    true
                }
                else -> false
            }
        }
        binding.dropDownTaskList.setOnClickListener {
            val fragment = TaskListsMenuFragment.newInstance()
            fragment.show(childFragmentManager, "movetolist_fragment")
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTask(taskID!!)
        viewModel.task.observe(viewLifecycleOwner, { task ->
            viewModel.getTasksList(task.listID)
            display(task)
        })
        viewModel.taskList.observe(viewLifecycleOwner, { list ->
            binding.dropDownTaskList.text = SpannableStringBuilder(list.name)
        })
    }

    private fun display(task: Task) {
        binding.editTextTaskTitle.text = SpannableStringBuilder(task.title)
        binding.editTextTaskDetail.text = SpannableStringBuilder(task.detail)
        binding.checkboxTaskCompletion.isChecked = task.isCompleted
         if(task.isCompleted){
            binding.checkboxTaskCompletion.text = "Mark as UnCompleted"
        } else {
            binding.checkboxTaskCompletion.text = "Mark as Completed"
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateTask(
            Task(
                title = binding.editTextTaskTitle.text.toString(),
                detail = binding.editTextTaskDetail.text.toString(),
                isCompleted = binding.checkboxTaskCompletion.isChecked
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}