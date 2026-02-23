package com.example.tasksync.data.remote

import com.example.tasksync.data.model.Priority
import com.example.tasksync.data.model.Task
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val functions: FirebaseFunctions
) {

    suspend fun saveTask(userId: String, task: Task) {
        val taskMap = hashMapOf(
            "id" to task.id,
            "title" to task.title,
            "description" to task.description,
            "priority" to task.priority.name,
            "dueDate" to task.dueDate,
            "isCompleted" to task.isCompleted,
            "createdAt" to task.createdAt,
            "updatedAt" to task.updatedAt,
            "isSynced" to true
        )
        val data = hashMapOf(
            "userId" to userId,
            "task" to taskMap
        )
        functions.getHttpsCallable("saveTask").call(data).await()
    }

    suspend fun deleteTask(taskId: String) {
        val data = hashMapOf("taskId" to taskId)
        functions.getHttpsCallable("deleteTask").call(data).await()
    }

    suspend fun getTasks(userId: String): List<Task> {
        return try {
            val data = hashMapOf("userId" to userId)
            val result = functions.getHttpsCallable("getTasks").call(data).await()
            val resultList = result.data as? List<Map<String, Any>> ?: emptyList()
            resultList.mapNotNull { taskMap ->
                try {
                    Task(
                        id = taskMap["id"] as? String ?: return@mapNotNull null,
                        title = taskMap["title"] as? String ?: "",
                        description = taskMap["description"] as? String ?: "",
                        priority = try { Priority.valueOf(taskMap["priority"] as? String ?: "MEDIUM") } catch(e: Exception) { Priority.MEDIUM },
                        dueDate = (taskMap["dueDate"] as? Number)?.toLong(),
                        isCompleted = taskMap["isCompleted"] as? Boolean ?: false,
                        createdAt = (taskMap["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        updatedAt = (taskMap["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        isSynced = taskMap["isSynced"] as? Boolean ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
