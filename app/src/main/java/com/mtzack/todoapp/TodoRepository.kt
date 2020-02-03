package com.mtzack.todoapp

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// リポジトリの意味ちょっとよくわかってない
class TodoRepository(private val context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "todo"
    ).build()

//    削除以外とってくる
    suspend fun get(): List<Todo> {
        return withContext(Dispatchers.Default) {
            db.todoDao().findByNotDeleted(false)
        }
    }


    fun insert(todo: Todo) {
        GlobalScope.launch {
            db.todoDao().insert(todo)
        }
    }

    suspend fun update(todo: Todo) {
        return withContext(Dispatchers.Default) {
            db.todoDao().updateBy(todo.id, todo.text, todo.checked)
        }
    }

    fun delete(todo:Todo) {
        GlobalScope.launch {
            db.todoDao().delete(todo)
        }
    }
}