package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.data.local.preference.MySharedPreferences
import com.devkazonovic.projects.mytasks.databinding.AddNewlistFragmentBinding
import com.devkazonovic.projects.mytasks.databinding.ListsFragmentBinding
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.CategoriesAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TasksListsFragment : BottomSheetDialogFragment() {

    private var _binding: ListsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoriesAdapter

    @Inject
    lateinit var sharedPreferences: MySharedPreferences

    private val viewModel by viewModels<TasksViewModel>(
        { requireParentFragment() }
    )


    companion object {
        fun newInstance() = TasksListsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListsFragmentBinding.inflate(layoutInflater)
        initRecyclerView()
        binding.cardViewAddNewList.setOnClickListener {
            createNewList()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCategories()
        viewModel.tasksLists.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    private fun initRecyclerView() {
        binding.recyclerViewTasksLists.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoriesAdapter(sharedPreferences) {
            viewModel.updateCurrentCategory(it.id)
            dismiss()
        }

        binding.recyclerViewTasksLists.adapter = adapter
    }

    private fun createNewList() {
        val view = AddNewlistFragmentBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("List Name")
        }

        builder.setView(view.root)
        builder.apply {
            setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
            setPositiveButton("Create") { dialog, which ->
                viewModel.createNewCategory(view.editTextListName.text.toString())
            }
        }.show()
    }
}