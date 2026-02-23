package com.example.tasksync.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasksync.R

class TaskReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskTitle = inputData.getString("task_title") ?: "Task Reminder"
        val taskId = inputData.getString("task_id") ?: ""

        showNotification(taskTitle, taskId)

        return Result.success()
    }

    private fun showNotification(title: String, taskId: String) {
        val builder = NotificationCompat.Builder(applicationContext, "task_reminders")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Reminder: $title")
            .setContentText("You have a task due soon!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(taskId.hashCode(), builder.build())
        }
    }
}
