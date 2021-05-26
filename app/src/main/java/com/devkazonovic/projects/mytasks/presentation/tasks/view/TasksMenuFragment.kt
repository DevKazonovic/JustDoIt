package com.devkazonovic.projects.mytasks.presentation.tasks.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.MyTasksApplication
import com.devkazonovic.projects.mytasks.data.TasksRepositoryImpl
import com.devkazonovic.projects.mytasks.databinding.AddNewlistFragmentBinding
import com.devkazonovic.projects.mytasks.databinding.TasksMenuFragmentBinding
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TasksMenuFragment : BottomSheetDialogFragment() {

    private var _binding: TasksMenuFragmentBinding? = null
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
        fun newInstance() = TasksMenuFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TasksMenuFragmentBinding.inflate(layoutInflater)

        binding.actionRename.setOnClickListener {
            updateCurrentListName()
            dismiss()
        }
        binding.actionDeleteList.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setMessage("Are You sure ?")
                setNegativeButton("Cancel") { dialog, which ->

                }
                setPositiveButton("Yes!") { dialog, which ->
                    viewModel.deleteCurrentList()
                    dismiss()
                }
            }.show()

        }
        binding.actionDeleteCompletedTasks.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setMessage("Are You sure ?")
                setNegativeButton("Cancel") { dialog, which ->

                }
                setPositiveButton("Yes!") { dialog, which ->
                    viewModel.deleteAllCompletedTasks()
                    dismiss()
                }
            }.show()

        }

        return binding.root
    }

    private fun updateCurrentListName() {
        val view = AddNewlistFragmentBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("List Name")
        }

        builder.setView(view.root)
        builder.apply {
            view.editTextListName.text =
                SpannableStringBuilder(viewModel.currentTaskList.value?.name)
            setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
            setPositiveButton("Update") { dialog, which ->
                viewModel.updateCurrentListName(view.editTextListName.text.toString())
            }
        }.show()
    }
}