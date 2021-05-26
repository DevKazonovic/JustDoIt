package com.devkazonovic.projects.mytasks.presentation.tasks.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.MyTasksApplication
import com.devkazonovic.projects.mytasks.data.TasksRepositoryImpl
import com.devkazonovic.projects.mytasks.databinding.AddNewtaskFragmentBinding
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddNewTaskFragment : BottomSheetDialogFragment() {

    private var _binding: AddNewtaskFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by viewModels(
        { requireParentFragment() },
        {
            TasksViewModelFactory(
                TasksRepositoryImpl((requireActivity().application as MyTasksApplication).dao)
            )
        }
    )

    companion object {
        fun newInstance() = AddNewTaskFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddNewtaskFragmentBinding.inflate(layoutInflater)
        binding.buttonSave.setOnClickListener {
            viewModel.saveTask(
                title = binding.editTextTaskDetail.text.toString(),
                detail = binding.editTextTaskDetail.text.toString()
            )
        }
        return binding.root
    }

}