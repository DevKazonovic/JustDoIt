package com.devkazonovic.projects.mytasks.data.local.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface BaseCrudDao<T> {

    @Insert
    fun insert(obj: T): Completable

    @Insert
    fun insertAndReturnId(obj: T): Single<Long>

    @Insert
    fun insert(vararg obj: T): Completable

    @Update
    fun update(obj: T): Completable

    @Delete
    fun delete(obj: T): Completable
}