package com.example.tasksync.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasksync.data.model.Priority
import com.example.tasksync.data.model.Task
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: String?,
    viewModel: TaskViewModel,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var isCompleted by remember { mutableStateOf(false) }

    val existingTask by produceState<Task?>(initialValue = null, taskId) {
        if (taskId != null) {
            value = viewModel.getTaskById(taskId)
        }
    }

    LaunchedEffect(existingTask) {
        existingTask?.let {
            title = it.title
            description = it.description
            priority = it.priority
            isCompleted = it.isCompleted
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "New Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Text("Priority", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val task = existingTask?.copy(
                        title = title,
                        description = description,
                        priority = priority,
                        isCompleted = isCompleted,
                        updatedAt = System.currentTimeMillis()
                    ) ?: Task(
                        title = title,
                        description = description,
                        priority = priority
                    )
                    coroutineScope.launch {
                        if (taskId == null) viewModel.addTask(task) else viewModel.updateTask(task)
                        onBackClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Save Task")
            }
        }
    }
}
