package com.devkazonovic.projects.mytasks.presentation.tasks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.data.local.preference.MySharedPreferences
import com.devkazonovic.projects.mytasks.databinding.CardSampleCategoryBinding
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.help.util.getThemeColor
import com.devkazonovic.projects.mytasks.presentation.tasks.diff.CategoriesDiffCallback

class SampleCategoriesAdapter(
    private val sharedPreferences: MySharedPreferences,
    private val onClick: (list: Category) -> Unit,
) : ListAdapter<Category, SampleCategoriesAdapter.ListViewHolder>(CategoriesDiffCallback()) {

    class ListViewHolder(
        private val sharedPreferences: MySharedPreferences,
        private val binding: CardSampleCategoryBinding,
        private val onClick: (list: Category) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        val context: Context = binding.root.context
        fun bind(list: Category) {
            binding.textViewCategoryName.text = list.name
            if (sharedPreferences.getCurrentTasksList() == list.id) {
                sharedPreferences.saveCurrentTasksList(list.id)
                binding.cardView.setCardBackgroundColor(getThemeColor(context, R.attr.colorPrimaryVariant))
            } else {
                binding.cardView.setCardBackgroundColor(getThemeColor(context, R.attr.colorSurface))
            }
            binding.root.setOnClickListener {
                onClick(list)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            CardSampleCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(sharedPreferences, binding) {
            onClick(it)
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}
