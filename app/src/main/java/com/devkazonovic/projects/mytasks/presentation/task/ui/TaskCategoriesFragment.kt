package com.devkazonovic.projects.mytasks.presentation.task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.databinding.FragmentTaskSelectCategoryBinding
import com.devkazonovic.projects.mytasks.presentation.task.TaskViewModel
import com.devkazonovic.projects.mytasks.presentation.task.adapter.TaskListsMenuAdapter
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.CategoriesDiffCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TaskCategoriesFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTaskSelectCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskListsMenuAdapter

    private val viewModel by viewModels<TaskViewModel>({ requireParentFragment() })

    companion object {
        fun newInstance() = TaskCategoriesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskSelectCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.category.observe(viewLifecycleOwner, { list ->
            Timber.d("$list")
            viewModel.getCategories()
            viewModel.lists.observe(viewLifecycleOwner, { lists ->
                setUpRecyclerView(list.id)
                adapter.submitList(lists)
            })
        })
    }

    private fun setUpRecyclerView(longID: Long) {
        binding.recyclerViewTaskCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskListsMenuAdapter(longID, CategoriesDiffCallback()) { taskList ->
            viewModel.updateCurrentTaskList(taskList.id)
        }
        binding.recyclerViewTaskCategories.adapter = adapter
    }

}