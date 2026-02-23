package com.example.tasksync.data.remote

import com.example.tasksync.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val tasksCollection = firestore.collection("tasks")

    suspend fun saveTask(userId: String, task: Task) {
        tasksCollection.document(task.id)
            .set(task.copy(isSynced = true))
            .await()
    }

    suspend fun deleteTask(taskId: String) {
        tasksCollection.document(taskId).delete().await()
    }

    suspend fun getTasks(userId: String): List<Task> {
        return try {
            val snapshot = tasksCollection.get().await()
            snapshot.toObjects(Task::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
