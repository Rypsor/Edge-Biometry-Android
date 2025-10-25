package com.ml.mindloop.facenet_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ml.mindloop.facenet_android.presentation.screens.add_face.AddFaceScreen
import com.ml.mindloop.facenet_android.presentation.screens.database.DatabaseScreen
import com.ml.mindloop.facenet_android.presentation.screens.detect_screen.DetectScreen
import com.ml.mindloop.facenet_android.presentation.screens.face_list.FaceListScreen
import com.ml.mindloop.facenet_android.presentation.screens.history.HistoryScreen
import com.ml.mindloop.facenet_android.presentation.screens.main_screen.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            NavHost(
                navController = navHostController,
                startDestination = "main",
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
            ) {
                composable("main") {
                    MainScreen(
                        onRegisterEntryClick = { navHostController.navigate("detect/entrada") },
                        onRegisterExitClick = { navHostController.navigate("detect/salida") },
                        onDatabaseClick = { navHostController.navigate("database") }
                    )
                }
                composable("database") {
                    DatabaseScreen(
                        onWorkersClick = { navHostController.navigate("face-list") },
                        onHistoryClick = { navHostController.navigate("history") },
                        onNavigateBack = { navHostController.navigateUp() }
                    )
                }
                composable("history") {
                    HistoryScreen { navHostController.navigateUp() }
                }
                composable(
                    route = "add-face?personId={personId}",
                    arguments = listOf(navArgument("personId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    })
                ) { backStackEntry ->
                    AddFaceScreen(
                        navController = navHostController,
                        onNavigateBack = { navHostController.navigateUp() },
                        personId = backStackEntry.arguments?.getLong("personId") ?: -1L
                    )
                }
                composable(
                    route = "detect/{eventType}",
                    arguments = listOf(navArgument("eventType") { type = NavType.StringType })
                ) { backStackEntry ->
                    val eventType = backStackEntry.arguments?.getString("eventType") ?: ""
                    DetectScreen(
                        eventType = eventType,
                        onNavigateBack = { navHostController.navigateUp() },
                        onOpenFaceListClick = { navHostController.navigate("face-list") }
                    )
                }
                composable("face-list") {
                    FaceListScreen(
                        onNavigateBack = { navHostController.navigateUp() },
                        onAddFaceClick = { navHostController.navigate("add-face") },
                        onEditFaceClick = { personId ->
                            navHostController.navigate("add-face?personId=$personId")
                        }
                    )
                }
            }
        }
    }
}
