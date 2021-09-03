package com.devkazonovic.projects.justdoit.presentation.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.CardCategoryBinding
import com.devkazonovic.projects.justdoit.domain.model.Category
import com.devkazonovic.projects.justdoit.help.extension.hide
import com.devkazonovic.projects.justdoit.help.extension.show
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.diff.CategoriesDiffCallback

class CategoriesAdapter(
    private val onClick: (view: View, category: Category) -> Unit,
) : ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(CategoriesDiffCallback()) {

    class CategoryViewHolder(
        private val binding: CardCategoryBinding,
        private val onClick: (view: View, category: Category) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        val context: Context = binding.root.context
        fun bind(category: Category) {
            binding.textViewCategoryName.text = category.name
            binding.textViewCategoryTasksNumber.text = "${category.tasksNumber}"
            if (category.isDefault) {
                binding.textViewCategoryType.show()
                binding.textViewCategoryType.text = context.getString(R.string.label_default)
            } else {
                binding.textViewCategoryType.hide()
            }

            binding.imageViewCategoryMenu.setOnClickListener {
                onClick(it, category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            CardCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding) { view, category -> onClick(view, category) }
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


}