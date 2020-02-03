package com.mtzack.todoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo")
    fun getAll(): List<Todo>

    @Query("SELECT * FROM todo where deleted_flag in (:on) order by id asc")
    fun findByNotDeleted(on: Boolean): List<Todo>

    @Insert
    fun insert(vararg todo: Todo)

    @Delete
    fun delete(todo: Todo)

    @Query("update todo set text = :text,checked = :check where id = :id")
    fun updateBy(id:Int,text:String,check:Boolean)
}