package com.example.tasksync

import com.example.tasksync.data.local.TaskDao
import com.example.tasksync.data.model.Priority
import com.example.tasksync.data.model.Task
import com.example.tasksync.data.remote.FirestoreService
import com.example.tasksync.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {
    private val taskDao: TaskDao = mockk(relaxed = true)
    private val firestoreService: FirestoreService = mockk(relaxed = true)
    private val auth: FirebaseAuth = mockk()
    private val firebaseUser: FirebaseUser = mockk()
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "test_user"
        repository = TaskRepository(taskDao, firestoreService, auth)
    }

    @Test
    fun `insertTask should call dao and firestore service`() = runBlocking {
        val task = Task(title = "Test Task", priority = Priority.HIGH)
        repository.insertTask(task)

        coVerify { taskDao.insertTask(task) }
        coVerify { firestoreService.saveTask("test_user", any()) }
    }

    @Test
    fun `deleteTask should call dao and firestore service`() = runBlocking {
        val task = Task(title = "Test Task")
        repository.deleteTask(task)

        coVerify { taskDao.deleteTask(task) }
        coVerify { firestoreService.deleteTask(task.id) }
    }
}
