package com.mtzack.todoapp

import android.content.Context


class ShowTodoListUseCase(private val context: Context) {
    private val repo = TodoRepository(context)
    suspend fun getTodoList(): List<Todo>? {
        return repo.get()
    }

    suspend fun add(todo: Todo): List<Todo> {
        repo.insert(todo)
        return repo.get()
    }

    suspend fun update(todo: Todo): List<Todo> {
        repo.update(todo)
        return repo.get()
    }

    suspend fun delete(todo: Todo): List<Todo> {
        repo.delete(todo)
        return repo.get()
    }
}
