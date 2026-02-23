package com.example.tasksync

import com.example.tasksync.data.model.Priority
import com.example.tasksync.data.model.Task
import com.example.tasksync.data.repository.TaskRepository
import com.example.tasksync.ui.tasks.SortOrder
import com.example.tasksync.ui.tasks.TaskViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TaskViewModelTest {
    private val repository: TaskRepository = mockk(relaxed = true)
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() {
        val tasks = listOf(
            Task(id = "1", title = "A", priority = Priority.LOW, createdAt = 100),
            Task(id = "2", title = "B", priority = Priority.HIGH, createdAt = 200)
        )
        every { repository.allTasks } returns flowOf(tasks)
        viewModel = TaskViewModel(repository)
    }

    @Test
    fun `tasks should be sorted by date by default`() = runTest {
        val tasks = viewModel.tasks.value
        assertEquals("2", tasks[0].id) // Most recent first
    }

    @Test
    fun `tasks should be sorted by priority when requested`() = runTest {
        viewModel.setSortOrder(SortOrder.PRIORITY)
        val tasks = viewModel.tasks.value
        assertEquals("1", tasks[0].id) // LOW < HIGH
    }
}
