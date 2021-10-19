package com.devkazonovic.projects.justdoit.presentation.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.FragmentCategoriesBinding
import com.devkazonovic.projects.justdoit.domain.model.Category
import com.devkazonovic.projects.justdoit.presentation.common.model.SortDirection
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentCategories : Fragment() {

    private lateinit var adapter: CategoriesAdapter
    private val viewModel by viewModels<CategoriesViewModel>()

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var navController: NavController

    @Inject
    lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        setUpListeners()
        setUpCategoriesList()
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categories?.let { adapter.submitList(it) }
        }

        viewModel.sort.observe(viewLifecycleOwner) {
            binding.viewSort.text = when (it) {
                CategorySort.DEFAULT -> getString(R.string.label_sort_default)
                CategorySort.NAME -> getString(R.string.label_sort_name)
            }
        }

        viewModel.order.observe(viewLifecycleOwner) {
            when (it) {
                SortDirection.ASC -> {
                    binding.viewSortDirection.setImageResource(R.drawable.ic_arrow_up)
                }
                SortDirection.DESC -> {
                    binding.viewSortDirection.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }

        viewModel.getCategories()
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveSortValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpToolBar() {
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setUpListeners() {
        binding.viewAddNewCategory.setOnClickListener {
            FormCreateCategoryFragment.newInstance().show(
                childFragmentManager,
                FormCreateCategoryFragment.TAG
            )
        }
        binding.viewSort.setOnClickListener {
            val enums = CategorySort.values()
            val enumsNames = enums.map {
                when (it) {
                    CategorySort.DEFAULT -> getString(R.string.label_sort_default)
                    CategorySort.NAME -> getString(R.string.label_sort_name)
                }
            }.toTypedArray()
            val checkedItem = enums.indexOf(viewModel.sort.value)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.label_sort_title))
                .setSingleChoiceItems(enumsNames, checkedItem) { dialog, which ->
                    viewModel.setSort(enums[which])
                }
                .show()
        }
        binding.viewSortDirection.setOnClickListener {
            viewModel.switchOrder()
        }
    }

    private fun setUpCategoriesList() {
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoriesAdapter { view, category ->
            showMenu(view, category)
        }
        binding.recyclerViewCategories.adapter = adapter
        val decorator = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.recyclerViewCategories.addItemDecoration(decorator)

    }

    private fun showMenu(v: View, category: Category) {
        viewModel.setSelectedCategory(category)
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(R.menu.menu_category, popup.menu)
        if (category.isDefault) {
            popup.menu.findItem(R.id.action_category_delete).isEnabled = false
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_category_edit -> {
                    FormEditCategoryFragment.newInstance()
                        .show(childFragmentManager, FormEditCategoryFragment.TAG)
                    true
                }

                R.id.action_category_delete -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.label_confirmation_title))
                        .setNegativeButton(resources.getString(R.string.label_cancel)) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(resources.getString(R.string.label_yes)) { dialog, which ->
                            viewModel.deleteCategory()
                            dialog.dismiss()
                        }
                        .show()
                    true
                }

                else -> false
            }
        }

        // Show the popup menu.
        popup.show()
    }


}