package com.example.tasksync.data.service

import android.content.Context
import com.example.tasksync.data.model.Task
import com.example.tasksync.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataExportService @Inject constructor(
    private val repository: TaskRepository
) {
    suspend fun exportTasksToJson(context: Context): File {
        val tasks = repository.allTasks.first()
        val jsonArray = JSONArray()
        tasks.forEach { task ->
            val jsonObject = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("priority", task.priority.name)
                put("isCompleted", task.isCompleted)
                put("createdAt", task.createdAt)
            }
            jsonArray.put(jsonObject)
        }

        val file = File(context.cacheDir, "tasks_export.json")
        file.writeText(jsonArray.toString(4))
        return file
    }
}
