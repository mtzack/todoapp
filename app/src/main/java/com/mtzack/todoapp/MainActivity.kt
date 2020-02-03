package com.mtzack.todoapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mtzack.todoapp.databinding.ActivityMainBinding
import com.mtzack.todoapp.databinding.ItemViewBinding

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TodoListViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this


        val viewModel =
            ViewModelProviders.of(this, TodoListViewModel.Factory(ShowTodoListUseCase(this)))
                .get(TodoListViewModel::class.java)

        binding.viewModel = viewModel

        val manager = LinearLayoutManager(this)

        binding.list.layoutManager = manager
        binding.list.adapter = TodoAdapter(viewModel)

        viewModel.onCreate()


        fab.setOnClickListener { view ->
            viewModel.todoList.value?.size?.let { binding.list.scrollToPosition(it) }
            GlobalScope.launch {
                viewModel.add("Sample")

            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_clear -> {
                viewModel.clear()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class TodoAdapter(private val viewModel: TodoListViewModel) :
    RecyclerView.Adapter<TodoAdapter.ItemViewHolder>() {

    private var todoList: List<Todo> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(parent)
    }

    override fun getItemCount(): Int = todoList.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (todoList.size > position) {
            holder.bind(todoList[position])
            holder.binding.delete.setOnClickListener {
                viewModel.delete(todoList[position])
            }
            holder.binding.checked.setOnClickListener{
                val todo = todoList[position]
                todo.checked =!todo.checked
                GlobalScope.launch { viewModel.update(todo) }
            }
            holder.binding.text.setOnEditorActionListener { textView, i, keyEvent ->
                val todo = todoList[position]
                todo.text = textView.text.toString()
                GlobalScope.launch {
                    viewModel.update(todo)
                }
                true
            }
        }
    }

    fun update(todoList: List<Todo>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class ItemViewHolder(
        private val parent: ViewGroup,
        val binding: ItemViewBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_view,
                parent,
                false
            )
    ) : ViewHolder(binding.root) {
        fun bind(item: Todo) {
            binding.todo = item
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("items")
        fun RecyclerView.bindItems(items: List<Todo>?) {

            if (items == null) {
                return
            }

            val adapter = adapter as TodoAdapter
            adapter.update(items)
        }
    }
}
