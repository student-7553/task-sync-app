package com.example.tasksync.data.local

import androidx.room.TypeConverter
import com.example.tasksync.data.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}
