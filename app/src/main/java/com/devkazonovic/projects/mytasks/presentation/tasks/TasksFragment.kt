package com.devkazonovic.projects.mytasks.presentation.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentTasksBinding
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.help.extension.hide
import com.devkazonovic.projects.mytasks.help.extension.setupSnackBar
import com.devkazonovic.projects.mytasks.help.extension.show
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.TasksViewPagerAdapter
import com.devkazonovic.projects.mytasks.presentation.tasks.form.FormNewTaskFragment
import com.devkazonovic.projects.mytasks.presentation.tasks.menu.TasksMenuFragment
import com.devkazonovic.projects.mytasks.presentation.tasks.menu.TasksSelectCategoryFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel>()

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        setUpTabs()
        setUpListeners()
        observeData()
        observeErrors()
        observeToasts()
        viewModel.start()
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveSortValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpToolbar() {

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_categories -> {
                    findNavController().navigate(R.id.categories)
                    true
                }
                R.id.action_setting -> {
                    findNavController().navigate(R.id.setting)
                    true
                }

                else -> false
            }
        }
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
            FormNewTaskFragment.newInstance()
                .show(childFragmentManager, FormNewTaskFragment.TAG)
        }
        binding.bottomAppBar.setNavigationOnClickListener {
            TasksSelectCategoryFragment.newInstance()
                .show(childFragmentManager, TasksSelectCategoryFragment.TAG)
        }
        binding.bottomAppBar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.action_show_menu -> {
                    TasksMenuFragment.newInstance()
                        .show(childFragmentManager, TasksMenuFragment.TAG)
                    true
                }
                else -> false
            }
        }

        binding.viewError.buttonActionOnError.setOnClickListener {
            viewModel.observeTasks()
        }


    }

    private fun observeData() {
        viewModel.currentCategory.observe(viewLifecycleOwner) {
            onCurrentCategoryChange(it)
        }


    }

    private fun observeToasts() {
        binding.fab.setupSnackBar(
            viewLifecycleOwner,
            viewModel.snackBarEvent,
            Snackbar.LENGTH_SHORT
        )
    }

    private fun observeErrors() {
        viewModel.mainViewErrorEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { error ->
                onError(error) {

                }
            }
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

}