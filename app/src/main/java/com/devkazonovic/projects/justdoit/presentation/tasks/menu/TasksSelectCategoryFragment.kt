package com.devkazonovic.projects.justdoit.presentation.tasks.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.justdoit.data.local.preference.MySharedPreferences
import com.devkazonovic.projects.justdoit.databinding.FragmentTasksSelectCategoryBinding
import com.devkazonovic.projects.justdoit.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.SampleCategoriesAdapter
import com.devkazonovic.projects.justdoit.presentation.tasks.form.FormNewCategoryFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TasksSelectCategoryFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<TasksViewModel>({ requireParentFragment() })

    private var _binding: FragmentTasksSelectCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SampleCategoriesAdapter

    @Inject
    lateinit var sharedPreferences: MySharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTasksSelectCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        binding.viewCreateNewCategory.setOnClickListener { createNewList() }
        viewModel.getCategories()
        viewModel.categories.observe(viewLifecycleOwner, { adapter.submitList(it) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        binding.recyclerViewTasksCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = SampleCategoriesAdapter(sharedPreferences) {
            viewModel.updateCurrentCategory(it.id)
            dismiss()
        }
        binding.recyclerViewTasksCategories.adapter = adapter
    }

    private fun createNewList() {
        FormNewCategoryFragment.newInstance()
            .show(childFragmentManager, FormNewCategoryFragment.TAG)
    }

    companion object {
        fun newInstance() = TasksSelectCategoryFragment()
        const val TAG = "Menu of User Categories To Change The Current Category"
    }
}