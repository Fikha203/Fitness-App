package com.example.fitnessapp.ui.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.ExerciseDetails
import com.example.fitnessapp.data.model.SetDetails


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanEditorScreen(
    planId: Int?, popBackStack: () -> Unit,
    onSave: () -> Unit, viewModel: PlanEditorViewModel = viewModel(
        factory = PlanEditorViewModel.Factory(
            LocalContext.current
        )
    )
) {

    val plan by viewModel.currentPlan.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()

    var showExerciseModal by remember { mutableStateOf(false) }

    LaunchedEffect(planId) {
        if (planId != null) {
            viewModel.loadPlan(planId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Edit Plan") },
                navigationIcon = {
                    IconButton(onClick = popBackStack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveCurrentPlan(); onSave() }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showExerciseModal = true },
                icon = { Icon(Icons.Filled.Add, stringResource(R.string.btn_add_exercise)) },
                text = { Text(text = stringResource(R.string.btn_add_exercise)) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Plan Name Input
            OutlinedTextField(
                value = plan.name,
                onValueChange = { viewModel.updatePlanName(it) },
                label = { Text("Plan Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Exercise List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(plan.exerciseDetails) { exerciseDetail ->
                    val exercise = viewModel.getExerciseById(exerciseDetail.exerciseId)
                    ExerciseCard(
                        exercise = exercise,
                        exerciseDetail = exerciseDetail,
                        onAddSet = { viewModel.addEmptySetToExercise(exerciseDetail.exerciseId) },
                        onUpdateSet = { setIndex, reps, weight ->
                            viewModel.updateSetForExercise(
                                exerciseId = exerciseDetail.exerciseId,
                                setIndex = setIndex,
                                reps = reps,
                                weight = weight
                            )
                        }
                    )
                }
            }
        }

        if (showExerciseModal) {
            ExerciseSelectionModal(
                exercises = allExercises,
                onDismiss = { showExerciseModal = false },
                onSubmit = { selectedIds ->
                    viewModel.addExercisesToPlan(selectedIds)
                    showExerciseModal = false
                }
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise?,
    exerciseDetail: ExerciseDetails,
    onAddSet: () -> Unit,
    onUpdateSet: (Int, Int?, Int?) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),

        ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = exercise?.name ?: "Unknown Exercise",
                style = MaterialTheme.typography.titleLarge
            )


            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(thickness = Dp.Hairline)

            Spacer(modifier = Modifier.height(8.dp))
            // Display Sets
            exerciseDetail.sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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
                }
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

@Composable
fun ExerciseSelectionModal(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSubmit: (List<Int>) -> Unit
) {
    val selectedIds = remember { mutableStateListOf<Int>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Exercises") },
        text = {
            LazyColumn {
                items(exercises) { exercise ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedIds.contains(exercise.id),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedIds.add(exercise.id)
                                } else {
                                    selectedIds.remove(exercise.id)
                                }
                            }
                        )
                        Text(
                            text = exercise.name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(selectedIds) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview()
@Composable
fun ExerciseSelectionModalPreview() {
    ExerciseSelectionModal(
        exercises = listOf(
            Exercise(1, "Bench Press"),
            Exercise(2, "Squat"),
            Exercise(3, "Deadlift"),
        ),
        onDismiss = {},
        onSubmit = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExerciseCardPreview() {
    LazyColumn {
        items(3) {
            ExerciseCard(
                exercise = Exercise(1, "Bench Press"),
                exerciseDetail = ExerciseDetails(
                    exerciseId = 1,
                    sets = listOf(
                        SetDetails(reps = 10, weight = 100),
                        SetDetails(reps = 10, weight = 100),
                        SetDetails(reps = 10, weight = 100),
                    )
                ),
                onAddSet = {},
                onUpdateSet = { _, _, _ -> }
            )

        }
    }

}