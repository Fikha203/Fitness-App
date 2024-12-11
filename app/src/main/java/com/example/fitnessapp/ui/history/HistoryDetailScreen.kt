package com.example.fitnessapp.ui.history

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.ExerciseDetails
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    historyId: Int,
    popBackStack: () -> Unit,
    viewModel: HistoryDetailViewModel = viewModel(
        factory = HistoryDetailViewModel.Factory(
            LocalContext.current
        )
    )
) {

    LaunchedEffect(historyId) {
        viewModel.getHistory(historyId)
    }

    val history = viewModel.history.collectAsState()
    val planName = viewModel.planName.collectAsState()
    val progressDetails = viewModel.progressDetails.collectAsState()
    val exerciseNames = viewModel.exerciseNames.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.title_detail_workout)) },
                navigationIcon = {
                    IconButton(onClick = popBackStack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Close")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    planName.value,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(history.value.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            LazyColumn(

                modifier = Modifier
                    .padding(16.dp)
            ) {
                items(progressDetails.value) { exerciseDetail ->
                    val exerciseName =
                        exerciseNames.value[exerciseDetail.exerciseId] ?: "Unknown Exercise"
                    HistoryExerciseCard(
                        exerciseName = exerciseName,
                        exerciseDetail = exerciseDetail,
                        onAddSet = { },
                        onUpdateSet = { _, _, _ -> },
                        onToggleCheck = {}
                    )
                }
            }

        }
    }
}

@Composable
fun HistoryExerciseCard(
    exerciseName: String,
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
                text = exerciseName,
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
                        enabled = false,
                        value = set.reps.toString(),
                        onValueChange = { newValue ->
                            val reps = newValue.toIntOrNull()
                            onUpdateSet(index, reps, null)
                        },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        enabled = false,
                        value = set.weight.toString(),
                        onValueChange = { newValue ->
                            val weight = newValue.toIntOrNull()
                            onUpdateSet(index, null, weight)
                        },
                        label = { Text("Weight") },
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        enabled = false,
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
                    enabled = false,
                    onClick = onAddSet,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("+ Add Set")
                }
            }
        }
    }
}

@Preview
@Composable
fun HistoryDetailViewModelPreview() {
    HistoryDetailScreen(1, {})
}

