package com.devkazonovic.projects.mytasks.presentation.task.ui

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentRepeatBinding
import com.devkazonovic.projects.mytasks.domain.model.Repeat
import com.devkazonovic.projects.mytasks.domain.model.RepeatType
import com.devkazonovic.projects.mytasks.help.extension.hide
import com.devkazonovic.projects.mytasks.help.extension.show
import com.devkazonovic.projects.mytasks.presentation.common.adapter.MaterialSpinnerAdapter
import com.devkazonovic.projects.mytasks.presentation.common.util.InputFilterMinMax
import com.devkazonovic.projects.mytasks.presentation.task.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RepeatDialogFragment : DialogFragment() {

    private val viewModel by viewModels<TaskViewModel>({ requireParentFragment() })


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Repeat")
        }
        val binding = FragmentRepeatBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        builder
            .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
            .setPositiveButton("Save") { dialog, which ->
                val number = binding.editTextRepeatNumber.text.toString()
                val type = binding.dropDownRepeatType.text.toString()
                if (type == getString(R.string.label_no_repeat)) {
                    viewModel.setRepeat(null)
                } else {
                    viewModel.setRepeat(
                        Repeat(
                            RepeatType.valueOf(type),
                            if (number.isEmpty()) 1 else number.toInt()
                        )
                    )
                }

            }
        setUpRepeatInput(binding)

        viewModel.repeat.observe(this) { repeat ->
            if (repeat == null) {
                binding.dropDownRepeatType.text =
                    SpannableStringBuilder(getString(R.string.label_no_repeat))
                binding.textInputRepeatNumber.hide()

            } else {
                binding.textInputRepeatNumber.show()
                binding.dropDownRepeatType.text =
                    SpannableStringBuilder(repeat.type?.name)
                binding.editTextRepeatNumber.text =
                    SpannableStringBuilder(repeat.number.toString())

            }
        }

        return builder.create()
    }

    private fun setUpRepeatInput(binding: FragmentRepeatBinding) {
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
                binding.textInputRepeatNumber.show()
                binding.editTextRepeatNumber.text = SpannableStringBuilder(value.toString())
            } else {
                binding.editTextRepeatNumber.text = SpannableStringBuilder("")
                binding.textInputRepeatNumber.hide()
            }
        }
        binding.editTextRepeatNumber.doOnTextChanged { text, start, before, count ->

        }
        binding.editTextRepeatNumber.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.editTextRepeatNumber.text = SpannableStringBuilder("1")
            }
        }
    }


    companion object {
        fun newInstance() = RepeatDialogFragment()
        const val TAG = "RepeatDialog"
    }

}