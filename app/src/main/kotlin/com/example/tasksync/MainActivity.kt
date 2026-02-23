package com.example.tasksync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.tasksync.ui.tasks.TaskEditScreen
import com.example.tasksync.ui.tasks.TaskListScreen
import com.example.tasksync.ui.theme.TaskSyncAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskSyncAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: com.example.tasksync.ui.tasks.TaskViewModel = hiltViewModel()
                    
                    NavHost(navController = navController, startDestination = "tasks") {
                        composable("tasks") {
                            TaskListScreen(
                                viewModel = viewModel,
                                onAddTaskClick = { navController.navigate("task_edit") },
                                onEditTaskClick = { taskId -> navController.navigate("task_edit?taskId=$taskId") }
                            )
                        }
                        composable(
                            route = "task_edit?taskId={taskId}",
                            arguments = listOf(navArgument("taskId") { 
                                type = NavType.StringType
                                nullable = true
                            })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId")
                            TaskEditScreen(
                                taskId = taskId,
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
