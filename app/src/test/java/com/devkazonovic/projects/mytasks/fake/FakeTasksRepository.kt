package com.devkazonovic.projects.mytasks.fake

import com.devkazonovic.projects.mytasks.data.repository.ITasksRepository
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.holder.Result
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.threeten.bp.OffsetDateTime

class FakeTasksRepository(
    private val tasks: MutableList<Task> = mutableListOf(),
    private val lists: MutableList<Category> = mutableListOf(
        Category(0, "MyList", true)
    )
) : ITasksRepository {


    override fun addNewCategory(category: Category): Single<Result<Long>> {
        lists.add(category)
        return Single.just(Result.Success((lists.size - 1).toLong()))
    }

    override fun updateCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override fun getCategoryById(listID: Long): Single<Result<Category>> {
        val foundedList = lists.find { it.id == listID }
        foundedList?.let {
            return Single.just(Result.Success(it))
        } ?: return Single.error(Exception("Task doesn't Exist"))
    }

    override fun getCategories(): Flowable<Result<List<Category>>> {
        TODO("Not yet implemented")
    }

    override fun addNewTask(task: Task): Completable {
        tasks.add(task)
        return Completable.complete()
    }

    override fun updateTask(task: Task): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteTask(task: Task): Completable {
        TODO("Not yet implemented")
    }

    override fun updateTaskReminder(taskID: Long, reminderDate: Long?): Completable {
        TODO("Not yet implemented")
    }

    override fun getTask(taskID: Long): Single<Result<Task>> {
        TODO("Not yet implemented")
    }

    override fun getAllTasks(): Single<Result<List<Task>>> {
        TODO("Not yet implemented")
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