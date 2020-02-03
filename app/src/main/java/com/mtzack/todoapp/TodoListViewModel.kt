package com.mtzack.todoapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class TodoListViewModel(private val showTodoListUseCase: ShowTodoListUseCase) : ViewModel() {

    // こいつが変更あるとリストViewに変更が走る。全県変更なので差分更新したほうがいい気がする。ちらつくのでうざい
    val todoList = MutableLiveData<List<Todo>>()

    fun onCreate() = GlobalScope.launch {
        val todo = showTodoListUseCase.getTodoList()
        todoList.postValue(todo)
    }

    fun clear() {
        val deleteList = todoList.value
        deleteList?.map {
            GlobalScope.launch {
                showTodoListUseCase.delete(it)
            }
        }
        todoList.value = emptyList()
    }

    suspend fun add(default: String) {
        todoList.postValue(GlobalScope.async {
            val list = showTodoListUseCase.getTodoList()
            val newId = list?.map { it.id }?.maxBy { it }?.plus(1) ?: 1 //UUIDにすりゃよかった
            val newTodo = Todo(newId, false, default, false)
            showTodoListUseCase.add(newTodo)
        }.await())
    }

    fun delete(todo: Todo) {
        GlobalScope.launch {
            todoList.postValue(showTodoListUseCase.delete(todo))
        }
    }

    suspend fun update(todo: Todo) {
        todoList.postValue(GlobalScope.async {
            showTodoListUseCase.update(todo)
        }.await())

    }

    class Factory(private val showTodoListUseCase: ShowTodoListUseCase) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TodoListViewModel(showTodoListUseCase) as T
        }
    }
}