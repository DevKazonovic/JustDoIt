package com.devkazonovic.projects.mytasks.presentation.tasks.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentTasksMenuBinding
import com.devkazonovic.projects.mytasks.help.extension.hide
import com.devkazonovic.projects.mytasks.help.extension.show
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.form.FormUpdateCategoryFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksMenuFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<TasksViewModel>({ requireParentFragment() })

    private var _binding: FragmentTasksMenuBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTasksMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        observeCategory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpListeners() {
        binding.itemRenameCategory.setOnClickListener {
            FormUpdateCategoryFragment.newInstance()
                .show(parentFragmentManager, FormUpdateCategoryFragment.TAG)
        }
        binding.itemDeleteCategory.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setMessage(getString(R.string.label_confirmation_title))
                setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }
                setPositiveButton(getString(R.string.label_yes)) { _, _ ->
                    viewModel.deleteCurrentCategory()
                    dismiss()
                }
            }.show()
        }
        binding.itemDeleteCompletedTasks.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setMessage(getString(R.string.label_confirmation_title))
                setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }
                setPositiveButton(getString(R.string.label_yes)) { _, _ ->
                    viewModel.deleteAllCompletedTasks()
                    dismiss()
                }
            }.show()

        }
    }

    private fun observeCategory() {
        viewModel.currentCategory.observe(viewLifecycleOwner) {
            it?.let { category ->
                if (category.isDefault) {
                    binding.itemDeleteCategory.isClickable = false
                    binding.imageViewDeleteCategoryIcon.isEnabled = false
                    binding.textViewDeleteCategoryTitle.isEnabled = false
                    binding.textViewDeleteCategoryDetail.isEnabled = false
                    binding.textViewDeleteCategoryDetail.show()
                    binding.textViewDeleteCategoryDetail.text = StringBuilder()
                        .append(getString(R.string.label_warning_delete_default_list))
                } else {
                    binding.textViewDeleteCategoryDetail.hide()
                }
            }
        }
        viewModel.completedTasks.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) {
                    binding.itemDeleteCompletedTasks.isClickable = false
                    binding.imageViewDeleteCompletedTasksIcon.isEnabled = false
                    binding.textViewDeleteCompletedTasksTitle.isEnabled = false
                    binding.textViewDeleteCompletedTasksDetail.isEnabled = false
                }
            }
        }
    }


    companion object {
        fun newInstance() = TasksMenuFragment()
        const val TAG = "Current Category Menu"
    }
}