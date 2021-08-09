package com.devkazonovic.projects.mytasks.presentation.task.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.CardSampleCategoryBinding
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.presentation.tasks.diff.CategoriesDiffCallback

class TaskCategoriesAdapter(
    private var _listID: Long? = null,
    diffCallback: CategoriesDiffCallback,
    private val onClick: (list: Category) -> Unit,
) : ListAdapter<Category, TaskCategoriesAdapter.ListViewHolder>(diffCallback) {


    class ListViewHolder(
        private val listID: Long? = null,
        private val binding: CardSampleCategoryBinding,
        private val onClick: (list: Category) -> Unit,

        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: Category) {
            binding.textViewCategoryName.text = list.name
            if (listID == list.id) {
                binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.brand
                    )
                )
            }
            binding.root.setOnClickListener {
                onClick(list)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            CardSampleCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(_listID, binding) {
            onClick(it)
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}
