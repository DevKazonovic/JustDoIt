package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentFormCategoryBinding
import com.devkazonovic.projects.mytasks.databinding.FragmentMenuCategoryBinding
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuCategoryFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMenuCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TasksViewModel>({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        observeCategory()
    }

    private fun observeCategory() {
        viewModel.currentCategory.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isDefault) {
                    disableView(binding.actionDeleteList)
                    binding.actionDeleteList.text = StringBuilder()
                        .append(binding.actionDeleteList.text)
                        .append("\n")
                        .append(getString(R.string.warning_delete_default_list))
                }
            }
        }

        viewModel.completedTasks.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) {
                    disableView(binding.actionDeleteCompletedTasks)
                }
            }
        }
    }

    private fun updateCurrentListName() {
        val view = FragmentFormCategoryBinding.inflate(layoutInflater)
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

    private fun setUpListeners() {
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
    }

    private fun disableView(view: TextView) {
        view.isClickable = false
        view.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.surface_gray)
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun newInstance() = MenuCategoryFragment()
    }
}