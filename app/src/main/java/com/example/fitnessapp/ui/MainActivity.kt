package com.example.fitnessapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnessapp.R
import com.example.fitnessapp.ui.exercise.ExerciseEditorScreen
import com.example.fitnessapp.ui.exercise.ExerciseScreen
import com.example.fitnessapp.ui.history.HistoryDetailScreen
import com.example.fitnessapp.ui.history.HistoryScreen
import com.example.fitnessapp.ui.home.HomeScreen
import com.example.fitnessapp.ui.navigation.NavigationItem
import com.example.fitnessapp.ui.plan.PlanDetailScreen
import com.example.fitnessapp.ui.plan.PlanEditorScreen
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.workout.WorkoutScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessAppTheme {
                setContent {
                    MainScreen()
                }
            }
        }
    }
}


@Composable
fun MainScreen(
    modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(bottomBar = {
        if (currentRoute == "home" || currentRoute == "exercise" || currentRoute == "history") {
            BottomBar(navController = navController)
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("home") {
                HomeScreen(navToDetailPlan = { planId -> navController.navigate("planDetail/${planId}") },
                    navToEditPlan = { planId -> navController.navigate("planEditor/${planId ?: -1}") })
            }

            composable(
                route = "planEditor/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.IntType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId")
                PlanEditorScreen(onSave = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                    planId = if (planId == -1) null else planId,
                    popBackStack = { navController.popBackStack() })
            }

            composable(
                route = "planDetail/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.IntType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId")
                if (planId != null) {
                    PlanDetailScreen(planId = planId,
                        popBackStack = { navController.popBackStack() },
                        navToWorkout = { navController.navigate("workout/${planId}") })
                }
            }



            composable("exercise") {
                ExerciseScreen(navToExerciseEditor = { exerciseId ->
                    navController.navigate("exerciseEditor/${exerciseId ?: -1}")
                })
            }

            composable(
                route = "exerciseEditor/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getInt("exerciseId")
                ExerciseEditorScreen(onSave = {
                    navController.navigate("exercise") {
                        popUpTo("exercise") { inclusive = true }
                    }
                },
                    exerciseId = if (exerciseId == -1) null else exerciseId,
                    popBackStack = { navController.popBackStack() }


                )
            }

            composable(
                route = "workout/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.IntType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId")
                if (planId != null) {
                    WorkoutScreen(
                        planId = planId,
                        popBackStack = { navController.popBackStack() },
                        navToHistory = {
                            navController.navigate("history") {
                                popUpTo("home") {   }
                            }
                        },
                    )
                }
            }

            composable("history") {
                HistoryScreen(navToHistoryDetail = { historyId ->
                    navController.navigate("historyDetail/${historyId}")
                })
            }

            composable(
                route = "historyDetail/{historyId}",
                arguments = listOf(navArgument("historyId") { type = NavType.IntType })
            ) { backStackEntry ->
                val historyId = backStackEntry.arguments?.getInt("historyId")
                if (historyId != null) {
                    HistoryDetailScreen(historyId = historyId,
                        popBackStack = { navController.popBackStack() })
                }
            }
        }
    }
}


@Composable
private fun BottomBar(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.menu_home),
                icon = Icons.Default.Home,
                screen = "home"
            ),
            NavigationItem(
                title = stringResource(R.string.menu_exercise),
                icon = Icons.Default.Build,
                screen = "exercise"
            ),
            NavigationItem(
                title = stringResource(R.string.menu_history),
                icon = Icons.Default.DateRange,
                screen = "history"
            ),
        )
        navigationItems.map { item ->
            NavigationBarItem(icon = {
                Icon(
                    imageVector = item.icon, contentDescription = item.title
                )
            }, label = { Text(item.title) },
                selected = currentRoute == item.screen, onClick = {
                    navController.navigate(item.screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                })
        }
    }
}
