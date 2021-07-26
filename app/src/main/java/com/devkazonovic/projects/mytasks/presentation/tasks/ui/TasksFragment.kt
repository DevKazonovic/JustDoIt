package com.devkazonovic.projects.mytasks.presentation.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentTasksBinding
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.help.extension.hide
import com.devkazonovic.projects.mytasks.help.extension.setupSnackBar
import com.devkazonovic.projects.mytasks.help.extension.show
import com.devkazonovic.projects.mytasks.presentation.tasks.TasksViewModel
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksViewPagerAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel>()

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTabs()
        setUpListeners()
        observeData()
        observeErrors()
        observeUserNotice()
    }

    private fun setUpTabs() {
        binding.viewpager.adapter = TasksViewPagerAdapter(
            requireActivity(), childFragmentManager, arrayOf(
                ActiveTasksFragment(),
                CompletedTasksFragment()
            )
        )

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.label_tab_active)
                1 -> getString(R.string.label_tab_completed)
                else -> ""
            }
        }.attach()
    }

    private fun setUpListeners() {
        binding.fab.setOnClickListener {
            FormNewTaskFragment.newInstance().show(childFragmentManager, "add_task_fragment")
        }
        binding.bottomAppBar.setNavigationOnClickListener {
            val fragment = MenuSelectCategoryFragment.newInstance()
            fragment.show(childFragmentManager, "tasks_lists_fragment")
        }
        binding.bottomAppBar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.action_show_menu -> {
                    MenuCategoryFragment.newInstance()
                        .show(childFragmentManager, "tasks_menu_fragment")
                    true
                }
                else -> false
            }
        }
    }

    private fun observeData() {
        viewModel.currentCategory.observeWithViewLifecycleOwner {
            onCurrentCategoryChange(it)
        }
    }

    private fun observeUserNotice() {
        binding.fab.setupSnackBar(
            viewLifecycleOwner,
            viewModel.snackBarEvent,
            Snackbar.LENGTH_SHORT
        )
    }

    private fun observeErrors() {
        viewModel.mainViewErrorEvent.observeWithViewLifecycleOwner {
            it.getContentIfNotHandled()?.let { error ->
                onError(error) {

                }
            }
        }
        viewModel.userInputErrorEvent.observeWithViewLifecycleOwner {

        }
        binding.fab.setupSnackBar(
            viewLifecycleOwner,
            viewModel.snackBarErrorEvent,
            Snackbar.LENGTH_SHORT
        )

    }

    private fun onError(message: Int, action: () -> Unit) {
        binding.viewData.hide()
        binding.viewError.root.show()
        binding.viewError.buttonActionOnError.setOnClickListener {
            action()
        }
        binding.viewError.textViewErrorMessage.text = getString(message)
    }

    private fun onCurrentCategoryChange(newCategory: Category) {
        newCategory.let {
            binding.toolbar.title = it.name
            binding.viewError.root.hide()
            binding.viewData.show()
            viewModel.observeTasks()
        }
    }


    private fun <T> LiveData<T>.observeWithViewLifecycleOwner(onChange: (T) -> Unit) {
        this.observe(viewLifecycleOwner, {
            onChange(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}