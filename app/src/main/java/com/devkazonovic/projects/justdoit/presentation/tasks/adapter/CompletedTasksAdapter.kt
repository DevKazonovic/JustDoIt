package com.devkazonovic.projects.justdoit.presentation.tasks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.justdoit.databinding.CardTaskBinding
import com.devkazonovic.projects.justdoit.domain.model.Task
import com.devkazonovic.projects.justdoit.help.extension.disable
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.diff.TasksDiffCallback
import com.devkazonovic.projects.justdoit.service.DateTimeHelper
import java.util.*

class CompletedTasksAdapter(
    diffCallback: TasksDiffCallback,
    private val onCheck: (task: Task) -> Unit,
    private val onClick: (taskID: Long) -> Unit,
    private val dateTimeHelper: DateTimeHelper,
) : ListAdapter<Task, CompletedTasksAdapter.TaskViewHolder>(diffCallback) {

    class TaskViewHolder(
        private val binding: CardTaskBinding,
        private val onCheck: (task: Task) -> Unit,
        private val onClick: (taskID: Long) -> Unit,
        private val dateTimeHelper: DateTimeHelper,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val context: Context = binding.root.context

        fun bind(task: Task) {
            binding.checkbox.isChecked = task.isCompleted
            binding.textViewTaskTitle.text = task.title
            binding.textViewReminder.text = ""
            binding.imageViewRepeatIcon.isVisible =
                task.repeatType != null && task.repeatValue != null
            binding.imageViewRepeatIcon.disable()
            binding.textViewTaskTitle.disable()
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheck(task)
                binding.checkbox.isChecked = !isChecked
            }
            binding.viewTask.setOnClickListener {
                onClick(task.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = CardTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, { onCheck(it) }, { onClick(it) }, dateTimeHelper)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    fun switchItems(fromPos: Int, toPos: Int) {
        val newList = currentList.toMutableList()
        Collections.swap(newList, toPos, fromPos)
        submitList(newList)
    }

    fun swipeTask(task: Task) {
        onCheck.invoke(task)
    }
}
