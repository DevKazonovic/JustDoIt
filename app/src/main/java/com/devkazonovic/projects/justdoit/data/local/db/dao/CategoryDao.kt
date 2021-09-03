package com.devkazonovic.projects.justdoit.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.devkazonovic.projects.justdoit.data.local.db.entity.CategoryEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface CategoryDao : BaseCrudDao<CategoryEntity> {


    @Query("SELECT * FROM category WHERE id = :listID")
    fun getCategoryById(listID: Long): Single<CategoryEntity>


    @Query("SELECT * FROM category")
    fun getCategories(): Flowable<List<CategoryEntity>>

}