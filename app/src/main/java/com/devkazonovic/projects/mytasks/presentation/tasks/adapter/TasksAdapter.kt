package com.devkazonovic.projects.mytasks.presentation.tasks.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.mytasks.databinding.CardTaskBinding
import com.devkazonovic.projects.mytasks.domain.model.Task
import java.util.*

class TasksAdapter(
    private val diffCallback: TasksDiffCallback,
    private val onCheck: (task: Task) -> Unit,
    private val onClick: (taskID: Long) -> Unit

) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(diffCallback) {

    class TaskViewHolder(
        private val binding: CardTaskBinding,
        private val onCheck: (task: Task) -> Unit,
        private val onClick: (taskID: Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            if (task.isCompleted) {
                binding.textViewTaskTitle.paintFlags =
                    binding.textViewTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.textViewTaskDetail.paintFlags =
                    binding.textViewTaskDetail.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            binding.checkbox.isChecked = task.isCompleted
            binding.textViewTaskTitle.text = "${task.title}"
            binding.textViewTaskDetail.text = "${task.detail}"
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                binding.checkbox.isChecked = !isChecked
                onCheck(task)
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
        })
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
