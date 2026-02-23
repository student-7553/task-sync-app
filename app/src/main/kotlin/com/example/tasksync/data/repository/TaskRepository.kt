package com.example.tasksync.data.repository

import com.example.tasksync.data.local.TaskDao
import com.example.tasksync.data.model.Task
import com.example.tasksync.data.remote.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val firestoreService: FirestoreService,
    private val auth: FirebaseAuth
) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
        syncTaskToCloud(task)
    }

    suspend fun updateTask(task: Task) {
        val updatedTask = task.copy(updatedAt = System.currentTimeMillis())
        taskDao.updateTask(updatedTask)
        syncTaskToCloud(updatedTask)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
        auth.currentUser?.uid?.let {
            firestoreService.deleteTask(task.id)
        }
    }

    private suspend fun syncTaskToCloud(task: Task) {
        auth.currentUser?.uid?.let { uid ->
            try {
                firestoreService.saveTask(uid, task)
                taskDao.updateTask(task.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced for later retry
            }
        }
    }

    suspend fun fetchTasksFromServer() {
        auth.currentUser?.uid?.let { uid ->
            try {
                val tasks = firestoreService.getTasks(uid)
                tasks.forEach { task ->
                    taskDao.insertTask(task.copy(isSynced = true))
                }
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }

    suspend fun getTaskById(id: String): Task? {
        return taskDao.getTaskById(id)
    }
}
