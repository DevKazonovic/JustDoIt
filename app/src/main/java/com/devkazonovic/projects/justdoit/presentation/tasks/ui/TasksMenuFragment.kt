package com.devkazonovic.projects.justdoit.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.FragmentTasksMenuBinding
import com.devkazonovic.projects.justdoit.help.extension.disable
import com.devkazonovic.projects.justdoit.help.extension.hide
import com.devkazonovic.projects.justdoit.help.extension.show
import com.devkazonovic.projects.justdoit.presentation.common.model.SortDirection
import com.devkazonovic.projects.justdoit.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.justdoit.presentation.tasks.model.TasksSort
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
        viewModel.sort.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewSortDetail.text = when (it) {
                    TasksSort.DEFAULT -> getString(R.string.label_sort_default)
                    TasksSort.DATE -> getString(R.string.label_sort_date)
                    TasksSort.NAME -> getString(R.string.label_sort_name)
                }
            }
        }

        viewModel.order.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    SortDirection.ASC -> {
                        binding.imageViewSortOrderIcon.setImageResource(R.drawable.ic_arrow_up)
                        binding.textViewSortOrderDetail.text = getString(R.string.label_asc)
                    }
                    SortDirection.DESC -> {
                        binding.imageViewSortOrderIcon.setImageResource(R.drawable.ic_arrow_down)
                        binding.textViewSortOrderDetail.text = getString(R.string.label_desc)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveSortValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpListeners() {
        binding.itemSort.setOnClickListener {
            val enums = TasksSort.values()
            val enumsNames = enums.map {
                when (it) {
                    TasksSort.DEFAULT -> getString(R.string.label_sort_default)
                    TasksSort.DATE -> getString(R.string.label_sort_date)
                    TasksSort.NAME -> getString(R.string.label_sort_name)
                }
            }.toTypedArray()
            val checkedItem = enums.indexOf(viewModel.sort.value)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.label_sort_title))
                .setSingleChoiceItems(enumsNames, checkedItem) { dialog, which ->
                    viewModel.setSort(enums[which])
                }
                .show()
        }
        binding.itemSortOrder.setOnClickListener {
            viewModel.switchOrder()
        }
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
        viewModel.activeTasks.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) {
                    binding.itemSort.isClickable = false
                    binding.itemSortOrder.isClickable = false
                    binding.imageViewSortIcon.disable()
                    binding.imageViewSortOrderIcon.disable()
                    binding.textViewSortTitle.disable()
                    binding.textViewSortDetail.disable()
                    binding.textViewSortOrderTitle.disable()
                    binding.textViewSortOrderDetail.disable()
                }
            }
        }
        viewModel.completedTasks.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) {
                    binding.itemDeleteCompletedTasks.isClickable = false
                    binding.imageViewDeleteCompletedTasksIcon.disable()
                    binding.textViewDeleteCompletedTasksTitle.disable()
                    binding.textViewDeleteCompletedTasksDetail.disable()
                }
            }
        }
    }

    companion object {
        fun newInstance() = TasksMenuFragment()
        const val TAG = "Current Category Menu"
    }
}