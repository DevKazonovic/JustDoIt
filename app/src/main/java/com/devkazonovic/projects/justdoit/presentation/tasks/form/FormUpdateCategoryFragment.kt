package com.devkazonovic.projects.justdoit.presentation.tasks.form

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.FragmentFormCategoryBinding
import com.devkazonovic.projects.justdoit.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.justdoit.presentation.tasks.ValidationViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

@AndroidEntryPoint
class FormUpdateCategoryFragment : DialogFragment() {

    private val disposable = CompositeDisposable()

    private val viewModel by viewModels<TasksViewModel>({ requireParentFragment() })
    private val validationVM by viewModels<ValidationViewModel>()

    private var _binding: FragmentFormCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
        _binding = FragmentFormCategoryBinding.inflate(layoutInflater)
        builder.setView(binding.root)

        viewModel.currentCategory.value?.let {
            binding.editTextCategoryName.text =
                SpannableStringBuilder(it.name)
        }
        validationVM.isCategoryLengthMax.observe(this) {
            it?.let {
                if (it) {
                    binding.textInputCategoryName.error = getString(R.string.category_error_length)
                } else {
                    binding.textInputCategoryName.error = null
                }
            }
        }

        validationVM.isCategoryNameEntered.observe(this) {
            it?.let { binding.buttonSave.isEnabled = it }
        }

        binding.editTextCategoryName.textChanges().skipInitialValue().subscribe {
            validationVM.categoryInputValidation(StringBuilder(it).toString())
        }.addTo(disposable)

        binding.buttonSave.setOnClickListener {
            viewModel.updateCurrentCategoryName(binding.editTextCategoryName.text.toString())
            dismiss()
        }

        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        _binding = null
        disposable.clear()
        validationVM.reset()
        super.onDismiss(dialog)
    }

    companion object {
        fun newInstance() = FormUpdateCategoryFragment()
        const val TAG = "Form To Update Current Category Name"
    }
}