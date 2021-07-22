package com.devkazonovic.projects.mytasks.presentation.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentAlarmBinding
import com.devkazonovic.projects.mytasks.help.extension.hide
import com.devkazonovic.projects.mytasks.help.extension.show
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.help.view.ViewTag.TAG_DATE_PICKER_DIALOGUE
import com.devkazonovic.projects.mytasks.help.view.ViewTag.TAG_TIME_PICKER_DIALOGUE
import com.devkazonovic.projects.mytasks.help.view.createDatePicker
import com.devkazonovic.projects.mytasks.help.view.createTimePicker
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalTime

private const val KEY_TASK_ID = "Task ID"
private var taskID: Long? = null

@AndroidEntryPoint
class ReminderFragment : DialogFragment() {

    private val viewModel by viewModels<ReminderViewModel>()
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentAlarmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListeners()
        observeData()
        viewModel.start(taskID!!)
    }

    private fun observeData() {
        viewModel.isDateTimeSelected.observe(this, {
            it.getContentIfNotHandled()?.let { isSelected ->
                if (!isSelected) {
                    binding.textViewError.isVisible = true
                    binding.textViewError.text = getString(R.string.errorEmptyDateTime)
                } else {
                    this.dismiss()
                }
            }
        })
        viewModel.dateStr.observe(this, {
            if (it != null) {
                binding.textViewDatePicker.text = it
                binding.viewClearDate.show()
            } else {
                binding.textViewDatePicker.text = getString(R.string.date_picker_add_date)
                binding.viewClearDate.hide()
            }

        })
        viewModel.timeStr.observe(this, {
            if (it != null) {
                binding.textViewTimePicker.text = it
                binding.viewClearTime.show()
            } else {
                binding.textViewTimePicker.text = getString(R.string.time_picker_add_time)
                binding.viewClearTime.hide()
            }
        })

        viewModel.isTaskHasReminder.observe(this, {
            binding.viewClearDate.isVisible = it
            binding.viewClearTime.isVisible = it

        })
    }

    private fun addListeners() {
        binding.viewAddDate.setOnClickListener {
            createDatePicker(
                viewModel.date.value ?: MaterialDatePicker.todayInUtcMilliseconds()
            ) { dateInMillis ->
                log("Date In Millis => $dateInMillis")
                viewModel.setDate(dateInMillis)
            }.show(childFragmentManager, TAG_DATE_PICKER_DIALOGUE)
        }
        binding.viewAddTime.setOnClickListener {
            createTimePicker(
                viewModel.time.value?.first ?: LocalTime.now().hour,
                viewModel.time.value?.second ?: LocalTime.now().minute,
                requireContext()
            ) { hour, minute ->
                log("Time => $hour : $minute")
                viewModel.setTime(Pair(hour, minute))
            }.show(childFragmentManager, TAG_TIME_PICKER_DIALOGUE)
        }
        binding.buttonDone.setOnClickListener {
            viewModel.setTaskReminder()
            setFragmentResult(
                "requestKey",
                bundleOf("bundleKey" to viewModel.timeMillis.value)
            )
        }
        binding.buttonCancel.setOnClickListener {
            this.dismiss()
        }
        binding.viewClearDate.setOnClickListener {
            viewModel.setDate(null)
        }
        binding.viewClearTime.setOnClickListener {
            viewModel.setTime(null)
        }

    }
}