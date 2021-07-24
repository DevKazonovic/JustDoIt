package com.devkazonovic.projects.mytasks.presentation.task.ui

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.TaskFragmentBinding
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.extension.showSnackBar
import com.devkazonovic.projects.mytasks.help.holder.DataState
import com.devkazonovic.projects.mytasks.help.holder.EventObserver
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.presentation.reminder.ReminderFragment
import com.devkazonovic.projects.mytasks.presentation.task.TaskViewModel
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val KEY_TASK_ID = "Task ID"
private const val KEY_NOTIFICATION_ID = "Notification ID"

private var taskID: Long? = null
private var notificationID: Int? = -1
private var reminder: Long? = null

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private var _binding: TaskFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TaskViewModel>()

    @Inject
    lateinit var navController: NavController

    @Inject
    lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var dateTimeHelper: DateTimeHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskID = it.getLong(KEY_TASK_ID,-1)
            notificationID = it.getInt(KEY_NOTIFICATION_ID,-1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TaskFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        cancelNotification()
        setToolBar()
        addListeners()
        observeData()
        observeDataState()
        observeNavigationEvents()
        viewModel.start(taskID!!)

    }

    override fun onPause() {
        super.onPause()
        updateTask()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setToolBar() {
        /*val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)*/
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun addListeners() {
        childFragmentManager.setFragmentResultListener(
            "requestKey",
            viewLifecycleOwner
        ) { _, bundle ->
            reminder = bundle.getLong("bundleKey")
            reminder?.let {
                if (it != 0L)
                    binding.textViewAddReminder.text = dateTimeHelper.showDateTime(it)
            }
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    viewModel.deleteTask()
                    true
                }
                else -> false
            }
        }
        binding.dropDownTaskList.setOnClickListener {
            val fragment = TaskListsMenuFragment.newInstance()
            fragment.show(childFragmentManager, "movetolist_fragment")
        }
        binding.checkboxTaskCompletion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxTaskCompletion.text = "Mark as UnCompleted"
            } else {
                binding.checkboxTaskCompletion.text = "Mark as Completed"
            }
            updateTaskTextDetail(isChecked)
            binding.checkboxTaskCompletion.isChecked = isChecked
        }
        binding.viewAddReminder.setOnClickListener {
            ReminderFragment().apply {
                arguments = bundleOf(KEY_TASK_ID to taskID)
            }.show(childFragmentManager, "Add Reminder")
        }
        binding.viewClearReminder.setOnClickListener {
            viewModel.removeReminder()
        }
    }

    private fun cancelNotification(){
        log("Notification : $notificationID")
        notificationID?.let {
            if(it!=-1)
                viewModel.cancelNotification(it)
        }


    }

    private fun observeData() {
        viewModel.task.observe(viewLifecycleOwner, { task ->
            reminder = task.reminderDate
            viewModel.getCategory(task.listID)
            displayTask(task)
        })
        viewModel.category.observe(viewLifecycleOwner, { list ->
            binding.dropDownTaskList.text = SpannableStringBuilder(list.name)
        })
    }

    private fun observeDataState() {
        viewModel.dataState.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is DataState.ErrorState -> {
                    binding.root.showSnackBar(
                        getString(it.messageID),
                        Toast.LENGTH_LONG
                    )
                }

                is DataState.ToastState -> {
                    Toast.makeText(requireActivity(), getString(it.messageID), Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    private fun observeNavigationEvents() {
        viewModel.navigateBack.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigateUp()
            }
        })
    }

    private fun displayTask(task: Task) {
        binding.editTextTaskTitle.text = SpannableStringBuilder(task.title)
        binding.editTextTaskDetail.text = SpannableStringBuilder(task.detail)
        updateTaskTextDetail(task.isCompleted)
        binding.textViewAddReminder.text =
            task.reminderDate?.let { dateTimeHelper.showDateTime(it) } ?: "Add Date/Time"
        binding.checkboxTaskCompletion.isChecked = task.isCompleted
        if (task.isCompleted) {
            binding.checkboxTaskCompletion.text = "Mark as UnCompleted"
        } else {
            binding.checkboxTaskCompletion.text = "Mark as Completed"
        }
    }

    private fun updateTaskTextDetail(isCompleted: Boolean) {
        if (isCompleted) {
            binding.editTextTaskTitle.paintFlags =
                binding.editTextTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.editTextTaskDetail.paintFlags =
                binding.editTextTaskDetail.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.editTextTaskTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
            binding.editTextTaskDetail.paintFlags = Paint.ANTI_ALIAS_FLAG
        }
    }

    private fun updateTask() {
        viewModel.updateTask(
            binding.editTextTaskTitle.text.toString(),
            binding.editTextTaskDetail.text.toString(),
            binding.checkboxTaskCompletion.isChecked,
            reminder
        )
    }

}