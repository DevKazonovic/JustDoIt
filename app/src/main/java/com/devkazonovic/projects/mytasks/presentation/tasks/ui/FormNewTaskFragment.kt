package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.databinding.FragmentFormTaskBinding
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormNewTaskFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TasksViewModel>(
        { requireParentFragment() }
    )

    companion object {
        fun newInstance() = FormNewTaskFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isTaskTitleEntered.observe(viewLifecycleOwner) {
            it?.let { binding.buttonSave.isEnabled = it }
        }

        val editTextTaskTitleDisposable =
            binding.editTextTaskTitle.textChanges().skipInitialValue().subscribe {
                viewModel.taskInputValidation(StringBuilder(it).toString())
            }
        binding.buttonSave.setOnClickListener {
            viewModel.saveTask(
                title = binding.editTextTaskTitle.text.toString(),
                detail = binding.editTextTaskDetail.text.toString()
            )
            editTextTaskTitleDisposable.dispose()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}