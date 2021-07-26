package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.data.local.preference.MySharedPreferences
import com.devkazonovic.projects.mytasks.databinding.FragmentMenuSelectCategoryBinding
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.CategoriesAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuSelectCategoryFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMenuSelectCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoriesAdapter

    @Inject
    lateinit var sharedPreferences: MySharedPreferences

    private val viewModel by viewModels<TasksViewModel>({ requireParentFragment() })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuSelectCategoryBinding.inflate(layoutInflater)
        initRecyclerView()
        binding.cardViewAddNewList.setOnClickListener {
            createNewList()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCategories()
        viewModel.categories.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

    }

    private fun initRecyclerView() {
        binding.recyclerViewTasksCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoriesAdapter(sharedPreferences) {
            viewModel.updateCurrentCategory(it.id)
            dismiss()
        }

        binding.recyclerViewTasksCategories.adapter = adapter
    }

    private fun createNewList() {
        FormNewCategoryFragment.newInstance().show(
            childFragmentManager,
            "Form_New_Category"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MenuSelectCategoryFragment()
    }
}