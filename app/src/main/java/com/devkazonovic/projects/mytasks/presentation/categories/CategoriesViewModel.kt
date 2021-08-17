package com.devkazonovic.projects.mytasks.presentation.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.data.local.preference.IMainSharedPreference
import com.devkazonovic.projects.mytasks.data.repository.ITasksRepository
import com.devkazonovic.projects.mytasks.domain.IRxScheduler
import com.devkazonovic.projects.mytasks.domain.holder.Result
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.help.util.handleResult
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.presentation.common.model.SortDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val sharedPreference: IMainSharedPreference,
    private val tasksRepository: ITasksRepository,
    rxScheduler: IRxScheduler,
) : ViewModel() {

    private val mainScheduler = rxScheduler.mainScheduler()
    private val ioScheduler = rxScheduler.ioScheduler()
    private val disposableGeneral = CompositeDisposable()

    private val _categories = MutableLiveData<List<Category>>()
    private val _category = MutableLiveData<Category>()
    private val _sort = MutableLiveData<CategorySort>()
    private val _order = MutableLiveData<SortDirection>()

    val categories: LiveData<List<Category>> get() = _categories
    val category: LiveData<Category> get() = _category
    val sort: LiveData<CategorySort> get() = _sort
    val order: LiveData<SortDirection> get() = _order


    init {
        _sort.value = sharedPreference.getCategoriesSort()?.let {
            CategorySort.valueOf(it)
        } ?: CategorySort.DEFAULT

        _order.value = sharedPreference.getCategoriesSortOrder()?.let {
            SortDirection.valueOf(it)
        } ?: SortDirection.ASC
    }

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
                tasksRepository.getCategoryTasks(category.id)
                    .map { result ->
                        when (result) {
                            is Result.Success -> {
                                category.copy(tasksNumber = result.value.size)
                            }
                            is Result.Failure -> {
                                category.copy(tasksNumber = 0)
                            }
                        }
                    }
                    .toObservable()
            }
            .take(categories.size.toLong())
            .toSortedList { o1, o2 ->
                categoriesComparator(o1, o2)
            }
    }

    fun categoriesComparator(o1: Category, o2: Category): Int {
        return _sort.value?.let { sort ->
            _order.value?.let { direction ->
                when (sort) {
                    CategorySort.DEFAULT -> {
                        if (o1.createdAt == null || o2.createdAt == null) 0
                        else {
                            when (direction) {
                                SortDirection.DESC -> {
                                    o2.createdAt.compareTo(o1.createdAt)
                                }
                                SortDirection.ASC -> {
                                    o1.createdAt.compareTo(o2.createdAt)
                                }
                            }
                        }
                    }
                    CategorySort.NAME -> {
                        when (direction) {
                            SortDirection.DESC -> {
                                o2.name.compareTo(o1.name)
                            }
                            SortDirection.ASC -> {
                                o1.name.compareTo(o2.name)
                            }
                        }
                    }
                }
            }
        } ?: 0
    }

    fun setSelectedCategory(category: Category) {
        _category.value = category
    }

    fun setSort(sort: CategorySort) {
        _sort.value = sort
        getCategories()
    }

    fun switchOrder() {
        when (_order.value) {
            SortDirection.ASC -> {
                _order.value = SortDirection.DESC
            }
            SortDirection.DESC -> {
                _order.value = SortDirection.ASC
            }
        }
        getCategories()
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

    fun saveSortValues() {
        sharedPreference.saveCategoriesSort(_sort.value?.name ?: CategorySort.DEFAULT.name)
        sharedPreference.saveCategoriesSortOrder(_order.value?.name ?: SortDirection.ASC.name)
    }
}