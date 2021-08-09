package com.devkazonovic.projects.mytasks.presentation.categories

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.FragmentCategoriesBinding
import com.devkazonovic.projects.mytasks.domain.model.Category
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

        viewModel.getCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setUpToolBar() {
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setUpListeners() {
        binding.cardViewAddNewList.setOnClickListener {
            FormCreateCategoryFragment.newInstance().show(
                childFragmentManager,
                FormCreateCategoryFragment.TAG
            )
        }
    }

    private fun setUpCategoriesList() {
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoriesAdapter { view, category ->
            showMenu(view, category)
        }
        binding.recyclerViewCategories.adapter = adapter
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