package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.databinding.AddNewlistFragmentBinding
import com.devkazonovic.projects.mytasks.databinding.TasksMenuFragmentBinding
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksMenuFragment : BottomSheetDialogFragment() {

    private var _binding: TasksMenuFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TasksViewModel>(
        { requireParentFragment() }
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
                    viewModel.deleteCurrentCategory()
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
                SpannableStringBuilder(viewModel.currentCategory.value?.name)
            setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
            setPositiveButton("Update") { dialog, which ->
                viewModel.updateCurrentCategoryName(view.editTextListName.text.toString())
            }
        }.show()
    }
}