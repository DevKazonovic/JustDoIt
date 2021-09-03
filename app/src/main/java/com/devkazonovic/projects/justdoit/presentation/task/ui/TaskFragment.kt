package com.devkazonovic.projects.justdoit.presentation.task.ui

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.FragmentTaskBinding
import com.devkazonovic.projects.justdoit.domain.holder.DataState
import com.devkazonovic.projects.justdoit.domain.holder.EventObserver
import com.devkazonovic.projects.justdoit.domain.model.RepeatType
import com.devkazonovic.projects.justdoit.domain.model.Task
import com.devkazonovic.projects.justdoit.help.extension.*
import com.devkazonovic.projects.justdoit.help.util.log
import com.devkazonovic.projects.justdoit.presentation.common.util.ViewTag
import com.devkazonovic.projects.justdoit.presentation.common.view.createDatePicker
import com.devkazonovic.projects.justdoit.presentation.common.view.createTimePicker
import com.devkazonovic.projects.justdoit.presentation.task.TaskViewModel
import com.devkazonovic.projects.justdoit.service.DateTimeHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime
import javax.inject.Inject


private const val KEY_TASK_ID = "Task ID"
private const val KEY_NOTIFICATION_ID = "Notification ID"

private var taskID: Long? = null
private var notificationID: Int? = -1

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private val viewModel by viewModels<TaskViewModel>()

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var navController: NavController

    @Inject
    lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var dateTimeHelper: DateTimeHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskID = it.getLong(KEY_TASK_ID, -1)
            notificationID = it.getInt(KEY_NOTIFICATION_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTaskBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelNotification()
        setUpToolBar()
        setUpTask()
        setUpTaskCategory()
        setUpTaskCheckBox()
        setUpTaskDueDate()
        observeEvents()

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

    private fun setUpToolBar() {
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_task_delete -> {
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setMessage(getString(R.string.label_confirmation_title))
                        setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }
                        setPositiveButton(getString(R.string.label_yes)) { _, _ ->
                            viewModel.deleteTask()
                        }
                    }.show()
                    true
                }
                else -> false
            }
        }
    }

    private fun setUpTask() {
        viewModel.currentTask.observe(viewLifecycleOwner, { task ->
            task?.let { showTask(it) }
        })
    }

    private fun setUpTaskCheckBox() {
        binding.checkboxTaskIsCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxTaskIsCompleted.text = getString(R.string.label_task_uncompleted)
                onTaskCompleted()
            } else {
                binding.checkboxTaskIsCompleted.text = getString(R.string.label_task_completed)
                onTaskActive()
            }
            binding.checkboxTaskIsCompleted.isChecked = isChecked
        }
    }

    private fun setUpTaskCategory() {
        binding.textViewTaskCategory.setOnClickListener {
            CategoriesDialogFragment.newInstance()
                .show(childFragmentManager, CategoriesDialogFragment.TAG)
        }

        binding.imageViewDropDown.setOnClickListener {
            CategoriesDialogFragment.newInstance()
                .show(childFragmentManager, CategoriesDialogFragment.TAG)
        }

        viewModel.currentTaskCategory.observe(viewLifecycleOwner, { list ->
            binding.textViewTaskCategory.text = list.name
        })
    }

    private fun setUpTaskDueDate() {
        dueDateInputListeners()
        viewModel.dateStr.observe(viewLifecycleOwner, {
            if (it != null) {

                binding.viewAddTime.enable()
                binding.textViewTimePicker.enable()
                binding.viewAddRepeat.enable()
                binding.textViewRepeat.enable()
                binding.viewClearTime.enable()
                binding.viewClearRepeat.enable()
                binding.viewClearDate.show()
                binding.textViewDatePicker.text = it
                binding.textViewTimePicker.text = getString(R.string.label_all_day)
            } else {
                binding.textViewDatePicker.text = getString(R.string.label_add_date)
                binding.viewAddTime.disable()
                binding.textViewTimePicker.disable()
                binding.viewAddRepeat.disable()
                binding.textViewRepeat.disable()
                binding.viewClearTime.disable()
                binding.viewClearRepeat.disable()
                binding.viewClearDate.hide()


            }
        })
        viewModel.timeStr.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.textViewTimePicker.text = it
                binding.viewClearTime.show()
            } else {
                if (viewModel.dateStr.value != null) {
                    binding.textViewTimePicker.text = getString(R.string.label_all_day)
                } else {
                    binding.textViewTimePicker.text = getString(R.string.label_add_time)
                }
                binding.viewClearTime.hide()
            }
        })
        viewModel.repeat.observe(viewLifecycleOwner) { repeat ->
            if (repeat != null) {
                binding.textViewRepeat.text = if (repeat.type == null && repeat.number == null) {
                    getString(R.string.label_no_repeat)
                } else {
                    showRepeatValue(repeat.type!!, repeat.number!!)
                }
            } else {
                binding.textViewRepeat.text = getString(R.string.label_no_repeat)
            }
        }
    }

    private fun dueDateInputListeners() {
        binding.textViewDatePicker.setOnClickListener {
            createDatePicker(
                viewModel.date.value ?: MaterialDatePicker.todayInUtcMilliseconds()
            ) { dateInMillis ->
                log("Date In Millis => $dateInMillis")
                viewModel.setDate(dateInMillis)
            }.show(childFragmentManager, ViewTag.TAG_DATE_PICKER_DIALOGUE)
        }
        binding.textViewTimePicker.setOnClickListener {
            createTimePicker(
                viewModel.time.value?.first ?: LocalTime.now().hour,
                viewModel.time.value?.second ?: LocalTime.now().minute,
                requireContext(),
                viewModel.getTimeFormat()
            ) { hour, minute ->
                log("Time => $hour : $minute")
                viewModel.setTime(Pair(hour, minute))
            }.show(childFragmentManager, ViewTag.TAG_TIME_PICKER_DIALOGUE)
        }
        binding.textViewRepeat.setOnClickListener {
            RepeatDialogFragment.newInstance().show(childFragmentManager, RepeatDialogFragment.TAG)
        }
        binding.viewClearDate.setOnClickListener {
            viewModel.setDate(null)
            viewModel.setTime(null)
            viewModel.setRepeat(null)
        }
        binding.viewClearTime.setOnClickListener {
            viewModel.setTime(null)
        }
        binding.viewClearRepeat.setOnClickListener {
            binding.textViewRepeat.text =
                SpannableStringBuilder(getString(R.string.label_no_repeat))
            binding.viewClearRepeat.hide()
        }
    }

    private fun showRepeatValue(type: RepeatType, value: Int): String {
        val number = if (value == 1) "" else value.toString()
        return getString(R.string.label_repeat_value_param, when (type) {
            RepeatType.DAY -> resources.getQuantityString(R.plurals.day, value, number)
            RepeatType.WEEK -> resources.getQuantityString(R.plurals.week, value, number)
            RepeatType.MONTH -> resources.getQuantityString(R.plurals.month, value, number)
            RepeatType.YEAR -> resources.getQuantityString(R.plurals.year, value, number)
        })
    }

    private fun showTask(task: Task) {
        binding.editTextTaskTitle.text = SpannableStringBuilder(task.title)
        binding.editTextTaskDetail.text = SpannableStringBuilder(task.detail)

        if (task.isCompleted) {
            binding.checkboxTaskIsCompleted.text = getString(R.string.label_mark_uncompleted)
            onTaskCompleted()
        } else {
            binding.checkboxTaskIsCompleted.text = getString(R.string.label_mark_completed)
            onTaskActive()
        }
        binding.checkboxTaskIsCompleted.isChecked = task.isCompleted
    }

    private fun onTaskCompleted() {
        val taskTitleViews = arrayOf(
            binding.editTextTaskTitle,
            binding.editTextTaskDetail
        )
        val taskCategoryViews = arrayOf(
            binding.viewTaskCategory,
            binding.imageViewDropDown,
            binding.textViewTaskCategory,
        )
        val taskDateViews = arrayOf(
            binding.viewAddDate,
            binding.viewClearDate,
            binding.textViewDatePicker,
        )
        val taskTimeViews = arrayOf(
            binding.viewAddTime,
            binding.viewClearTime,
            binding.textViewTimePicker,
        )
        val taskRepeatViews = arrayOf(
            binding.viewAddRepeat,
            binding.viewClearRepeat,
            binding.textViewRepeat,
        )
        taskTitleViews.forEach { text ->
            text.paintFlags = binding.editTextTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        taskTitleViews.disable()
        taskCategoryViews.disable()
        taskDateViews.disable()
        taskTimeViews.disable()
        taskRepeatViews.disable()
    }

    private fun onTaskActive() {
        val taskTitleViews = arrayOf(
            binding.editTextTaskTitle,
            binding.editTextTaskDetail
        )
        val taskCategoryViews = arrayOf(
            binding.viewTaskCategory,
            binding.imageViewDropDown,
            binding.textViewTaskCategory,
        )
        val taskDateViews = arrayOf(
            binding.viewAddDate,
            binding.viewClearDate,
            binding.textViewDatePicker,
        )
        val taskTimeViews = arrayOf(
            binding.viewAddTime,
            binding.viewClearTime,
            binding.textViewTimePicker,
        )
        val taskRepeatViews = arrayOf(
            binding.viewAddRepeat,
            binding.viewClearRepeat,
            binding.textViewRepeat,
        )
        taskTitleViews.forEach { text ->
            text.paintFlags = Paint.ANTI_ALIAS_FLAG
        }
        taskTitleViews.enable()
        taskCategoryViews.enable()
        taskDateViews.enable()
        if (viewModel.date.value != null) {
            taskTimeViews.enable()
            taskRepeatViews.enable()
        } else {
            taskTimeViews.disable()
            taskRepeatViews.disable()
        }
    }

    private fun updateTask() {
        viewModel.updateTask(
            binding.editTextTaskTitle.text.toString(),
            binding.editTextTaskDetail.text.toString(),
            binding.checkboxTaskIsCompleted.isChecked,
        )
    }

    private fun cancelNotification() {
        notificationID?.let {
            if (it != -1) viewModel.cancelNotification(it)
        }
    }

    private fun observeEvents() {
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
        viewModel.navigateBack.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigateUp()
            }
        })
    }


}
