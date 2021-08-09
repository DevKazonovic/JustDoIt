package com.devkazonovic.projects.mytasks.presentation.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.data.repository.ITasksRepository
import com.devkazonovic.projects.mytasks.domain.IRxScheduler
import com.devkazonovic.projects.mytasks.domain.holder.Result
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.help.util.handleResult
import com.devkazonovic.projects.mytasks.help.util.log
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val tasksRepository: ITasksRepository,
    rxScheduler: IRxScheduler,
) : ViewModel() {

    private val mainScheduler = rxScheduler.mainScheduler()
    private val ioScheduler = rxScheduler.ioScheduler()
    private val disposableGeneral = CompositeDisposable()

    private val _categories = MutableLiveData<List<Category>>()
    private val _category = MutableLiveData<Category>()

    val categories: LiveData<List<Category>> get() = _categories
    val category: LiveData<Category> get() = _category


    fun getCategories() {
        tasksRepository.getCategories()
            .flatMapSingle { result ->
                when (result) {
                    is Result.Success -> {
                        calcCategoryTasks(result.value).map { Result.Success(it) }
                    }

                    is Result.Failure -> {
                        Single.just(result)
                    }
                }
            }
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(
                    result,
                    { _categories.postValue(it) },
                    { log("Categories : $it") }
                )
            }
    }

    private fun calcCategoryTasks(categories: List<Category>): Single<List<Category>> {
        return Observable.fromIterable(categories)
            .flatMap { category ->
                log("$category")
                tasksRepository.getCategoryTasks(category.id)
                    .map { result ->
                        when (result) {
                            is Result.Success -> {
                                log("${result.value.size}")
                                result.value.size
                            }

                            is Result.Failure -> {
                                0
                            }
                        }
                    }
                    .map { category.copy(tasksNumber = it) }
                    .toObservable()

            }
            .doAfterNext { log("Category $it") }
            .take(categories.size.toLong())
            .toList()
            .doAfterSuccess { log("calcCategoryTasks $it") }

    }

    fun setSelectedCategory(category: Category) {
        _category.value = category
    }

    fun editCategory(newName: String) {
        _category.value?.let {
            tasksRepository.updateCategory(it.copy(name = newName))
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    {},
                    { log("editCategory : $it") }
                ).addTo(disposableGeneral)
        }

    }

    fun deleteCategory() {
        _category.value?.let {
            tasksRepository.deleteCategory(it)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    {},
                    { log("deleteCategory : $it") }
                ).addTo(disposableGeneral)
        }
    }

    fun createCategory(name: String) {
        tasksRepository.addNewCategory(Category(name = name))
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { },
                { log("createCategory : $it") }
            ).addTo(disposableGeneral)
    }
}