package com.devkazonovic.projects.justdoit.presentation.tasks.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.FragmentFormTaskBinding
import com.devkazonovic.projects.justdoit.help.extension.disable
import com.devkazonovic.projects.justdoit.help.extension.enable
import com.devkazonovic.projects.justdoit.help.extension.hide
import com.devkazonovic.projects.justdoit.help.extension.show
import com.devkazonovic.projects.justdoit.help.util.log
import com.devkazonovic.projects.justdoit.presentation.common.util.ViewTag
import com.devkazonovic.projects.justdoit.presentation.common.view.createDatePicker
import com.devkazonovic.projects.justdoit.presentation.common.view.createTimePicker
import com.devkazonovic.projects.justdoit.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.justdoit.presentation.tasks.ValidationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import org.threeten.bp.LocalTime

@AndroidEntryPoint
class FormNewTaskFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<TasksViewModel>({ requireParentFragment() })
    private val validationVM by viewModels<ValidationViewModel>()

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setDate(null)
        viewModel.setTime(null)
        validationVM.isTaskTitleEntered.observe(this) {
            it?.let { binding.buttonSave.isEnabled = it }
        }
        viewModel.dateStr.observe(this) { date ->
            if (date != null) {
                binding.viewDueDate.show()
                binding.textViewDueDate.text = viewModel.timeStr.value?.let { time ->
                    "$date at $time"
                } ?: "$date"
                binding.imageViewTime.enable()
            } else {
                binding.viewDueDate.hide()
                binding.imageViewTime.disable()
            }
        }
        viewModel.timeStr.observe(this) { time ->

            viewModel.dateStr.value.let { date ->
                if (date != null) {
                    if (time != null) {
                        binding.textViewDueDate.text = "$date at $time"
                    } else {
                        binding.textViewDueDate.text = "$date"
                    }
                } else {
                    binding.viewDueDate.hide()
                    binding.imageViewTime.disable()
                }
            }
        }
        binding.editTextTaskTitle.textChanges().skipInitialValue().subscribe {
            validationVM.taskInputValidation(StringBuilder(it).toString())
        }.addTo(disposable)

        binding.imageViewDate.setOnClickListener {
            createDatePicker(
                requireContext(),
                viewModel.date.value ?: MaterialDatePicker.todayInUtcMilliseconds()
            ) { dateInMillis ->
                log("Date In Millis => $dateInMillis")
                viewModel.setDate(dateInMillis)
            }.show(childFragmentManager, ViewTag.TAG_DATE_PICKER_DIALOGUE)
        }
        binding.imageViewTime.setOnClickListener {
            createTimePicker(
                viewModel.time.value?.first ?: LocalTime.now().hour,
                viewModel.time.value?.second ?: LocalTime.now().minute,
                requireContext(),
                viewModel.getTimeFormat(),
                { hour, minute ->
                    log("Time => $hour : $minute")
                    viewModel.setTime(Pair(hour, minute))
                }, {
                    viewModel.setTime(null)
                }).show(childFragmentManager, ViewTag.TAG_TIME_PICKER_DIALOGUE)
        }
        binding.buttonSave.setOnClickListener {
            viewModel.saveTask(
                title = binding.editTextTaskTitle.text.toString()
            )
            dismiss()
        }
        binding.imageViewClearDueDate.setOnClickListener {
            viewModel.setDate(null)
            viewModel.setTime(null)
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        _binding = null
        disposable.clear()
        validationVM.reset()
        super.onDismiss(dialog)
    }


    companion object {
        fun newInstance() = FormNewTaskFragment()
        const val TAG = "Form To Adding New Task"
    }
}