package com.devkazonovic.projects.mytasks.presentation.tasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.databinding.CardTaskListBinding
import com.devkazonovic.projects.mytasks.domain.MySharedPreferences
import com.devkazonovic.projects.mytasks.domain.model.TaskList

class TasksListsAdapter(
    diffCallback: TasksListsDiffCallback,
    private val onClick: (list: TaskList) -> Unit
) : ListAdapter<TaskList, TasksListsAdapter.ListViewHolder>(diffCallback) {

    class ListViewHolder(
        private val binding: CardTaskListBinding,
        private val onClick: (list: TaskList) -> Unit

    ) : RecyclerView.ViewHolder(binding.root) {
        private val mySharedPreferences = MySharedPreferences(binding.root.context)
        fun bind(list: TaskList) {
            binding.textViewListName.text = "${list.name}"
            if(mySharedPreferences.getCurrentTasksList() == list.id) {
                mySharedPreferences.saveCurrentTasksList(list.id)

                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,
                    R.color.teal_200))
            }
            binding.root.setOnClickListener {
                onClick(list)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            CardTaskListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding) {
            onClick(it)
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}
