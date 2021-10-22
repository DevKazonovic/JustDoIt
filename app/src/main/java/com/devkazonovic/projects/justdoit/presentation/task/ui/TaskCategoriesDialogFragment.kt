package com.devkazonovic.projects.justdoit.presentation.task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.justdoit.databinding.FragmentTaskSelectCategoryBinding
import com.devkazonovic.projects.justdoit.presentation.task.TaskViewModel
import com.devkazonovic.projects.justdoit.presentation.task.adapter.TaskCategoriesAdapter
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.diff.CategoriesDiffCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskCategoriesDialogFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<TaskViewModel>({ requireParentFragment() })

    private var _binding: FragmentTaskSelectCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TaskCategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTaskSelectCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentTaskCategory.observe(viewLifecycleOwner, { list ->
            viewModel.getCategories()
            viewModel.categories.observe(viewLifecycleOwner, { lists ->
                setUpRecyclerView(list.id)
                adapter.submitList(lists)
            })
        })
    }

    private fun setUpRecyclerView(longID: Long) {
        binding.recyclerViewTaskCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskCategoriesAdapter(longID, CategoriesDiffCallback()) { taskList ->
            viewModel.updateTaskCategory(taskList.id)
        }
        binding.recyclerViewTaskCategories.adapter = adapter
    }

    companion object {
        fun newInstance() = TaskCategoriesDialogFragment()
        const val TAG = "Menu To Change Current Task Category"
    }
}