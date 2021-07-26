package com.devkazonovic.projects.mytasks.data.repository

import com.devkazonovic.projects.mytasks.domain.holder.Result
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class FakeTasksRepositoryTaskTest @Inject constructor() : ITasksRepository {

    private val tasks: MutableList<Task> = mutableListOf(
        Task(1, "Title1", "Detail"),
        Task(2, "Title1", "Detail", reminderDate = 1626429600000),
    )

    private val categories: MutableList<Category> = mutableListOf(
        Category(0, "MyList", true)
    )

    override fun addNewCategory(category: Category): Single<Result<Long>> {
        categories.add(category)
        return Single.just(Result.Success((categories.size - 1).toLong()))
    }

    override fun updateCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override fun getCategoryById(listID: Long): Single<Result<Category>> {
        val foundedList = categories.find { it.id == listID }
        foundedList?.let {
            return Single.just(Result.Success(it))
        } ?: return Single.error(Exception("Task doesn't Exist"))
    }

    override fun getCategories(): Flowable<Result<List<Category>>> {
        return Flowable.just(Result.Success(categories))
    }

    override fun addNewTask(task: Task): Completable {
        tasks.add(task)
        return Completable.complete()
    }

    override fun updateTask(task: Task): Completable {
        val findTask = tasks.findIndex(task.id)
        tasks[findTask] = task
        return Completable.complete()
    }

    override fun deleteTask(task: Task): Completable {
        tasks.removeAt(tasks.findIndex(task.id))
        return Completable.complete()
    }

    override fun updateTaskReminder(taskID: Long, reminderDate: Long?): Completable {
        val index = tasks.findIndex(taskID)
        return tasks.findTask(taskID)?.let {
            tasks[index] = it.copy(reminderDate = reminderDate)
            Completable.complete()
        } ?: Completable.error(Exception("Task Not Found"))

    }

    override fun getTask(taskID: Long): Single<Result<Task>> {
        return tasks.findTask(taskID)?.let {
            Single.just(Result.Success(it))
        } ?: Single.just(Result.Failure(Exception("Task Not Found")))
    }

    override fun getAllTasks(): Single<Result<List<Task>>> {
        return Single.just(Result.Success(tasks))
    }

    override fun getCompletedTasks(listID: Long): Flowable<Result<List<Task>>> {
        return Flowable.fromArray(tasks.filter { it.isCompleted }).map { Result.Success(it) }
    }

    override fun getUnCompletedTasks(listID: Long): Flowable<Result<List<Task>>> {
        return Flowable.fromArray(tasks.filter { !it.isCompleted }).map { Result.Success(it) }
    }

    override fun markTaskAsCompleted(taskId: Long, completedAt: OffsetDateTime): Completable {
        val foundedTask = tasks.find { it.id == taskId }
        foundedTask?.let {
            tasks.add(it.id.toInt(), it.copy(isCompleted = !it.isCompleted))
            tasks.remove(foundedTask)
            return Completable.complete()
        } ?: return Completable.error(Exception("Task doesn't Exist"))
    }

    override fun markTaskAsUnCompleted(taskId: Long): Completable {
        val foundedTask = tasks.find { it.id == taskId }
        foundedTask?.let {
            tasks.add(it.id.toInt(), it.copy(isCompleted = !it.isCompleted))
            tasks.remove(foundedTask)
            return Completable.complete()
        } ?: return Completable.error(Exception("Task doesn't Exist"))
    }

    override fun clearCompletedTasks(): Completable {
        TODO("Not yet implemented")
    }


}

fun List<Task>.findIndex(taskID: Long): Int {
    return this.indexOfFirst { it.id == taskID }
}

fun List<Task>.findTask(taskID: Long): Task? {
    return this.find { it.id == taskID }
}