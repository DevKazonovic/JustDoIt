package com.devkazonovic.projects.justdoit.presentation.tasks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.databinding.CardTaskBinding
import com.devkazonovic.projects.justdoit.databinding.CardTasksHeaderBinding
import com.devkazonovic.projects.justdoit.domain.model.Task
import com.devkazonovic.projects.justdoit.help.util.getThemeColor
import com.devkazonovic.projects.justdoit.presentation.tasks.adapter.diff.ActiveTasksDiffCallback
import com.devkazonovic.projects.justdoit.presentation.tasks.model.ActiveTask
import com.devkazonovic.projects.justdoit.service.DateTimeHelper

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class ActiveTasksAdapter(
    diffCallback: ActiveTasksDiffCallback,
    private val onCheck: (task: Task) -> Unit,
    private val onClick: (taskID: Long) -> Unit,
    private val dateTimeHelper: DateTimeHelper,
) : ListAdapter<ActiveTask, RecyclerView.ViewHolder>(diffCallback) {

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
            task.dueDate?.let {
                if (task.isAllDay) {
                    binding.textViewReminder.text = dateTimeHelper.showDate(it)
                    if (dateTimeHelper.isDateBeforeNow(task.dueDate)) {
                        binding.textViewReminder.setTextColor(getThemeColor(context,
                            R.attr.colorError))
                        binding.imageViewRepeatIcon.setColorFilter(getThemeColor(context,
                            R.attr.colorError))
                    } else {
                        binding.textViewReminder.setTextColor(getThemeColor(context,
                            R.attr.colorPrimary))
                        binding.imageViewRepeatIcon.setColorFilter(getThemeColor(context,
                            R.attr.colorPrimary))
                    }
                } else {
                    binding.textViewReminder.text = dateTimeHelper.showDateTime(it)
                    if (dateTimeHelper.isBeforeNow(task.dueDate)) {
                        binding.textViewReminder.setTextColor(getThemeColor(context,
                            R.attr.colorError))
                        binding.imageViewRepeatIcon.setColorFilter(getThemeColor(context,
                            R.attr.colorError))
                    } else {
                        binding.textViewReminder.setTextColor(getThemeColor(context,
                            R.attr.colorPrimary))
                        binding.imageViewRepeatIcon.setColorFilter(getThemeColor(context,
                            R.attr.colorPrimary))
                    }
                }
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

    class HeaderViewHolder(
        private val binding: CardTasksHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ActiveTask.ItemHeader) {
            val context = binding.root.context
            when (item.type) {
                ActiveTask.HeaderType.OVERDUE -> {
                    binding.textViewHeader.text = context.getString(R.string.label_overdue)
                    binding.textViewHeader.setTextColor(getThemeColor(context, R.attr.colorError))
                }
                ActiveTask.HeaderType.NO_DATE -> {
                    binding.textViewHeader.text = context.getString(R.string.label_no_due_date)
                    binding.textViewHeader.setTextColor(getThemeColor(context, R.attr.colorDisable))
                }
                ActiveTask.HeaderType.ACTIVE -> {
                    binding.textViewHeader.text = context.getString(R.string.label_active)
                    binding.textViewHeader.setTextColor(getThemeColor(context, R.attr.colorPrimary))

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> {
                val binding =
                    CardTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TaskViewHolder(binding, { onCheck(it) }, { onClick(it) }, dateTimeHelper)
            }
            ITEM_VIEW_TYPE_HEADER -> {
                val binding = CardTasksHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ActiveTask.ItemTask -> ITEM_VIEW_TYPE_ITEM
            is ActiveTask.ItemHeader -> ITEM_VIEW_TYPE_HEADER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskViewHolder -> {
                val item = getItem(position) as ActiveTask.ItemTask
                holder.bind(item.task)
            }
            is HeaderViewHolder -> {
                val item = getItem(position) as ActiveTask.ItemHeader
                holder.bind(item)

            }
        }
    }

}


