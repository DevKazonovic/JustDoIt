package com.devkazonovic.projects.mytasks.presentation.tasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.mytasks.databinding.CardTaskBinding
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
import java.util.*

class TasksAdapter(
    diffCallback: TasksDiffCallback,
    private val onCheck: (task: Task) -> Unit,
    private val onClick: (taskID: Long) -> Unit,
    private val dateTimeHelper: DateTimeHelper
) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(diffCallback) {

    class TaskViewHolder(
        private val binding: CardTaskBinding,
        private val onCheck: (task: Task) -> Unit,
        private val onClick: (taskID: Long) -> Unit,
        private val dateTimeHelper: DateTimeHelper
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            log("$task")
            binding.checkbox.isChecked = task.isCompleted
            binding.textViewTaskTitle.text = task.title
            task.reminderDate?.let {
                binding.textViewReminder.text = dateTimeHelper.showDateTime(it)
            }
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
        return TaskViewHolder(binding, {
            onCheck(it)
        }, {
            onClick(it)
        }, dateTimeHelper)
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
