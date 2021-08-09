package com.devkazonovic.projects.mytasks.presentation.task.ui

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentTaskBinding
import com.devkazonovic.projects.mytasks.domain.holder.DataState
import com.devkazonovic.projects.mytasks.domain.holder.EventObserver
import com.devkazonovic.projects.mytasks.domain.model.Repeat
import com.devkazonovic.projects.mytasks.domain.model.RepeatType
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.extension.hide
import com.devkazonovic.projects.mytasks.help.extension.show
import com.devkazonovic.projects.mytasks.help.extension.showSnackBar
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.help.view.ViewTag
import com.devkazonovic.projects.mytasks.help.view.createDatePicker
import com.devkazonovic.projects.mytasks.help.view.createTimePicker
import com.devkazonovic.projects.mytasks.presentation.common.InputFilterMinMax
import com.devkazonovic.projects.mytasks.presentation.common.MaterialSpinnerAdapter
import com.devkazonovic.projects.mytasks.presentation.task.TaskViewModel
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
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
        setUpTaskData()
        setUpTaskDueDate()

        observeStateEvents()
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

    private fun setUpTaskData() {
        binding.dropDownTaskCategory.setOnClickListener {
            TaskSelectCategoryFragment.newInstance()
                .show(childFragmentManager, TaskSelectCategoryFragment.TAG)
        }
        binding.checkboxTaskIsCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxTaskIsCompleted.text = getString(R.string.label_task_uncompleted)
            } else {
                binding.checkboxTaskIsCompleted.text = getString(R.string.label_task_completed)
            }
            updateTaskTextDetail(isChecked)
            binding.checkboxTaskIsCompleted.isChecked = isChecked
        }
        viewModel.currentTask.observe(viewLifecycleOwner, { task ->
            task?.let { displayTask(it) }
        })
        viewModel.currentTaskCategory.observe(viewLifecycleOwner, { list ->
            binding.dropDownTaskCategory.text = SpannableStringBuilder(list.name)
        })
    }

    private fun setUpTaskDueDate() {
        setUpRepeatInput()
        setUpDateTimeInput()
        viewModel.dateStr.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.textViewDatePicker.text = it
                binding.viewContainerRepeat.show()
                binding.viewAddTime.show()
                binding.viewClearDate.show()
                binding.textViewTimePicker.text = getString(R.string.label_all_day)

            } else {
                binding.textViewDatePicker.text = getString(R.string.label_add_date)
                binding.dropDownRepeatType.text = SpannableStringBuilder(
                    getString(R.string.label_no_repeat)
                )
                binding.editTextRepeatNumber.text = SpannableStringBuilder("")
                binding.viewClearDate.hide()
                binding.viewAddTime.hide()
                binding.viewContainerRepeat.hide()
            }
        })
        viewModel.timeStr.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.textViewTimePicker.text = it
                binding.viewClearTime.show()
            } else {
                binding.textViewTimePicker.text = getString(R.string.label_all_day)
                binding.viewClearTime.hide()
            }
        })
        viewModel.repeat.observe(viewLifecycleOwner) { repeat ->
            if (repeat == null) {
                binding.dropDownRepeatType.text =
                    SpannableStringBuilder(getString(R.string.label_repeat_value_param))
                binding.viewClearRepeat.hide()
                binding.textViewRepeatValue.hide()
            } else {
                binding.dropDownRepeatType.text =
                    SpannableStringBuilder(repeat.type?.name)
                binding.editTextRepeatNumber.text =
                    SpannableStringBuilder(repeat.number.toString())

            }
        }
    }

    private fun setUpDateTimeInput() {
        binding.viewAddDate.setOnClickListener {
            createDatePicker(
                viewModel.date.value ?: MaterialDatePicker.todayInUtcMilliseconds()
            ) { dateInMillis ->
                log("Date In Millis => $dateInMillis")
                viewModel.setDate(dateInMillis)
            }.show(childFragmentManager, ViewTag.TAG_DATE_PICKER_DIALOGUE)
        }
        binding.viewAddTime.setOnClickListener {
            createTimePicker(
                viewModel.time.value?.first ?: LocalTime.now().hour,
                viewModel.time.value?.second ?: LocalTime.now().minute,
                requireContext()
            ) { hour, minute ->
                log("Time => $hour : $minute")
                viewModel.setTime(Pair(hour, minute))
            }.show(childFragmentManager, ViewTag.TAG_TIME_PICKER_DIALOGUE)
        }
        binding.viewClearDate.setOnClickListener {
            viewModel.setDate(null)

        }
        binding.viewClearTime.setOnClickListener {
            viewModel.setTime(null)
        }
    }

    private fun setUpRepeatInput() {
        val items = arrayOf(
            getString(R.string.label_no_repeat),
            RepeatType.DAY.name,
            RepeatType.WEEK.name,
            RepeatType.MONTH.name,
            RepeatType.YEAR.name

        )
        val adapter = MaterialSpinnerAdapter(
            requireContext(),
            android.R.layout.simple_selectable_list_item,
            items
        )
        binding.dropDownRepeatType.setAdapter(adapter)
        binding.editTextRepeatNumber.filters = arrayOf(InputFilterMinMax(1, 99))
        binding.dropDownRepeatType.setOnItemClickListener { parent, view, position, id ->
            val str = parent.getItemAtPosition(position) as String
            if (str != getString(R.string.label_no_repeat)) {
                val text = binding.editTextRepeatNumber.text
                binding.editTextRepeatNumber.text = SpannableStringBuilder("1")
                val value = if (text.isNullOrEmpty()) 1 else text.toString().toInt()
                val item = RepeatType.valueOf(str)
                binding.textInputRepeatNumber.show()
                binding.viewClearRepeat.show()
                binding.textViewRepeatValue.text = showRepeatValue(item, value)
            } else {
                binding.textViewRepeatValue.text = getString(R.string.label_no_repeat)
                binding.editTextRepeatNumber.text = SpannableStringBuilder("")
                binding.textInputRepeatNumber.hide()
                binding.viewClearRepeat.hide()
            }
        }
        binding.editTextRepeatNumber.doOnTextChanged { text, start, before, count ->
            if (binding.dropDownRepeatType.text.toString() != getString(R.string.label_no_repeat)) {
                val value = if (text.isNullOrEmpty()) 1 else text.toString().toInt()
                val item = RepeatType.valueOf(binding.dropDownRepeatType.text.toString())
                binding.textViewRepeatValue.text = showRepeatValue(item, value)
            }
        }
        binding.editTextRepeatNumber.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.editTextRepeatNumber.text = SpannableStringBuilder("1")
            }
        }
        binding.viewClearRepeat.setOnClickListener {
            binding.dropDownRepeatType.text =
                SpannableStringBuilder(getString(R.string.label_no_repeat))
            binding.textViewRepeatValue.text = ""
            binding.textInputRepeatNumber.hide()
            binding.viewClearRepeat.hide()
        }
    }

    private fun showRepeatValue(type: RepeatType, value: Int): String {
        val number = if (value == 1) {
            ""
        } else value.toString()
        return getString(R.string.label_repeat_value_param, when (type) {
            RepeatType.DAY -> resources.getQuantityString(R.plurals.day, value, number)
            RepeatType.WEEK -> resources.getQuantityString(R.plurals.week, value, number)
            RepeatType.MONTH -> resources.getQuantityString(R.plurals.month, value, number)
            RepeatType.YEAR -> resources.getQuantityString(R.plurals.year, value, number)
        })
    }

    private fun displayTask(task: Task) {
        binding.editTextTaskTitle.text = SpannableStringBuilder(task.title)
        binding.editTextTaskDetail.text = SpannableStringBuilder(task.detail)
        updateTaskTextDetail(task.isCompleted)

        binding.checkboxTaskIsCompleted.isChecked = task.isCompleted
        if (task.isCompleted) {
            binding.checkboxTaskIsCompleted.text = getString(R.string.label_mark_uncompleted)
        } else {
            binding.checkboxTaskIsCompleted.text = getString(R.string.label_mark_completed)
        }

        task.repeatType?.let {
            binding.textInputRepeatNumber.show()
            binding.viewClearRepeat.show()
            binding.dropDownRepeatType.text = SpannableStringBuilder(it.name)
            binding.editTextRepeatNumber.text = SpannableStringBuilder(task.repeatValue.toString())
        } ?: run {
            binding.dropDownRepeatType.text =
                SpannableStringBuilder(getString(R.string.label_no_repeat))
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
        if (binding.dropDownRepeatType.text.toString() == getString(R.string.label_no_repeat)) {
            viewModel.updateTask(
                binding.editTextTaskTitle.text.toString(),
                binding.editTextTaskDetail.text.toString(),
                binding.checkboxTaskIsCompleted.isChecked,
                Repeat(null, null)
            )
        } else {
            val repeatValue = binding.editTextRepeatNumber.text?.toString()
            val repeatType = RepeatType.valueOf(binding.dropDownRepeatType.text.toString())
            viewModel.updateTask(
                binding.editTextTaskTitle.text.toString(),
                binding.editTextTaskDetail.text.toString(),
                binding.checkboxTaskIsCompleted.isChecked,
                Repeat(
                    repeatType,
                    if (repeatValue.isNullOrEmpty()) 1 else repeatValue.toInt()
                )
            )
        }


    }

    private fun cancelNotification() {
        notificationID?.let {
            if (it != -1) viewModel.cancelNotification(it)
        }
    }

    private fun observeStateEvents() {
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

}
