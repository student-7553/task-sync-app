package com.example.tasksync.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasksync.data.model.Task
import com.example.tasksync.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder {
    DATE, PRIORITY
}

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.DATE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    val tasks: StateFlow<List<Task>> = combine(
        repository.allTasks,
        _sortOrder
    ) { tasks, sortOrder ->
        when (sortOrder) {
            SortOrder.DATE -> tasks.sortedByDescending { it.createdAt }
            SortOrder.PRIORITY -> tasks.sortedBy { it.priority }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    suspend fun getTaskById(id: String): Task? {
        return repository.getTaskById(id)
    }
}
