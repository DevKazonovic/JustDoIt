package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.mytasks.databinding.FragmentFormCategoryBinding
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormNewCategoryFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFormCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TasksViewModel>(
        { requireParentFragment() }
    )

    companion object {
        fun newInstance() = FormNewCategoryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormCategoryBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isCategoryNameEntered.observe(this) {
            it?.let { binding.buttonSave.isEnabled = it }
        }

        val editTextListNameDisposable =
            binding.editTextListName.textChanges().skipInitialValue().subscribe {
                viewModel.categoryInputValidation(StringBuilder(it).toString())
            }

        binding.buttonSave.setOnClickListener {
            viewModel.createNewCategory(binding.editTextListName.text.toString())
            editTextListNameDisposable.dispose()
            dismiss()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}