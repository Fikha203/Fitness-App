package com.example.fitnessapp.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.ExerciseDetails
import com.example.fitnessapp.data.model.SetDetails


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    planId: Int?,
    popBackStack: () -> Unit,
    navToHistory: () -> Unit,
    viewModel: WorkoutViewModel = viewModel(
        factory = WorkoutViewModel.Factory(
            LocalContext.current
        )
    )
) {
    val plan by viewModel.currentPlan.collectAsState()

    LaunchedEffect(planId) {
        if (planId != null) {
            viewModel.loadPlan(planId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Workout") },
                navigationIcon = {
                    IconButton(onClick = popBackStack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Close")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.saveHistory(); navToHistory() },
                icon = { Icon(Icons.Filled.Check, stringResource(R.string.btn_add_plan)) },
                text = { Text(text = stringResource(R.string.btn_finish_workout)) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(

                modifier = Modifier
                    .padding(16.dp)
            ) {
                items(plan.exerciseDetails) { exerciseDetail ->
                    WorkoutExerciseCard(
                        exercise = viewModel.getExerciseById(exerciseDetail.exerciseId),
                        exerciseDetail = exerciseDetail,
                        onAddSet = { viewModel.addEmptySetToExercise(exerciseDetail.exerciseId) },
                        onUpdateSet = { setIndex, reps, weight ->
                            viewModel.updateSetForExercise(
                                exerciseId = exerciseDetail.exerciseId,
                                setIndex = setIndex,
                                reps = reps,
                                weight = weight
                            )
                        },
                        onToggleCheck = { setIndex ->
                            viewModel.toggleSetCompletion(
                                exerciseId = exerciseDetail.exerciseId,
                                setIndex = setIndex
                            )
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun WorkoutExerciseCard(
    exercise: Exercise?,
    exerciseDetail: ExerciseDetails,
    onAddSet: () -> Unit,
    onUpdateSet: (Int, Int?, Int?) -> Unit,
    onToggleCheck: (Int) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White,
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = exercise?.name ?: "Unknown Exercise",
                style = MaterialTheme.typography.titleLarge
            )

            exerciseDetail.sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = set.reps.toString(),
                        onValueChange = { newValue ->
                            val reps = newValue.toIntOrNull()
                            onUpdateSet(index, reps, null)
                        },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = set.weight.toString(),
                        onValueChange = { newValue ->
                            val weight = newValue.toIntOrNull()
                            onUpdateSet(index, null, weight)
                        },
                        label = { Text("Weight") },
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = set.isComplete,
                        onCheckedChange = { onToggleCheck(index) }
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = onAddSet,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("+ Add Set")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WorkoutExerciseCardPreview() {
    LazyColumn {
        items(3) {
            WorkoutExerciseCard(
                exercise = Exercise(1, "Bench Press"),
                exerciseDetail = ExerciseDetails(
                    exerciseId = 1,
                    sets = listOf(
                        SetDetails(reps = 10, weight = 100, isComplete = true),
                        SetDetails(reps = 10, weight = 100, isComplete = true),
                        SetDetails(reps = 10, weight = 100, isComplete = false),
                    )
                ),
                onAddSet = {},
                onUpdateSet = { _, _, _ -> },
                onToggleCheck = {}
            )
        }
    }

}
