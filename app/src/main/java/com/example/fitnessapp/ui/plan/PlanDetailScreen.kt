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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun PlanDetailScreen(
    planId: Int,
    popBackStack: () -> Unit,
    navToWorkout: (Int) -> Unit,
    viewModel: PlanDetailViewModel = viewModel(
        factory = PlanDetailViewModel.Factory(
            LocalContext.current
        )
    )
) {

    LaunchedEffect(planId) {
        if (planId != null) {
            viewModel.getPlan(planId)
        }
    }

    val plan = viewModel.plan.collectAsState()
    val exercises = viewModel.exercises.collectAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Detail Plan") },
                navigationIcon = {
                    IconButton(onClick = popBackStack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Close")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navToWorkout(planId) },
                icon = { Icon(Icons.Filled.PlayArrow, stringResource(R.string.btn_start_workout)) },
                text = { Text(text = stringResource(R.string.btn_start_workout)) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            // Plan Name Input
            Text(text = plan.value.name, modifier = Modifier.padding(start = 16.dp), style = MaterialTheme.typography.displaySmall)

            Spacer(modifier = Modifier.height(16.dp))
            // Exercise List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(plan.value.exerciseDetails) { exerciseDetail ->
                    val exercise = exercises.value[exerciseDetail.exerciseId]
                    ExerciseCardDetail(
                        exerciseDetail = exerciseDetail,
                                exercise = exercise
                    )
                }
            }

        }
    }
}

@Composable
fun ExerciseCardDetail(
    exerciseDetail: ExerciseDetails,
    exercise: Exercise?
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
                text = exercise?.name ?: "Loading...",
                style = MaterialTheme.typography.titleLarge
            )


            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(thickness = 1.dp)

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
                        enabled = false,
                        value = set.reps.toString(),
                        onValueChange = { newValue ->
                            val reps = newValue.toIntOrNull()
                        },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        enabled = false,
                        value = set.weight.toString(),
                        onValueChange = { newValue ->
                            val weight = newValue.toIntOrNull()
                        },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExerciseCardDetailPreview() {
    ExerciseCardDetail(
        exercise = Exercise(
            id = 1,
            name = "Bench Press",
            description = "Chest"
        ),
        exerciseDetail = ExerciseDetails(
            exerciseId = 1,
            sets = listOf(
                SetDetails(reps = 10, weight = 100),
                SetDetails(reps = 10, weight = 100),
                SetDetails(reps = 10, weight = 100),
            )
        ),
    )
}