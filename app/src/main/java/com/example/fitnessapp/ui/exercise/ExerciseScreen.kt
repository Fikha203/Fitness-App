package com.example.fitnessapp.ui.exercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    navToExerciseEditor: (Any?) -> Unit,
    viewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModel.Factory(LocalContext.current))

) {


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Exercise")
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navToExerciseEditor(null) },
                icon = { Icon(Icons.Filled.Add, stringResource(R.string.btn_add_exercise)) },
                text = { Text(text = stringResource(R.string.btn_add_exercise)) },
            )
        }
    ) { innerPadding ->

        val exercises by viewModel.exerciseList.collectAsState()

        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(exercises) { exercise ->
                ExerciseItem(
                    exercise = exercise,
                    onClick = { navToExerciseEditor(exercise.id) },
                    onDelete = { viewModel.deleteExercise(exercise) })
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: Exercise, onClick: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = exercise.name, modifier = Modifier.weight(1f))

            Box{
                // Dropdown Icon
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.btn_more_options)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onDelete()
                        },
                        text = {
                            Text(text = stringResource(R.string.dropdown_delete))
                        }
                    )
                }
            }



        }
    }
}

@Composable
fun ConfirmDeleteExerciseDialog(
    exerciseName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                stringResource(R.string.dialog_title_delete, exerciseName)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                content = { Text(stringResource(R.string.btn_delete)) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                content = { Text(stringResource(R.string.btn_cancel)) }
            )
        },
        onDismissRequest = onDismiss
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExerciseItemPreview() {
    LazyColumn {
        items(5) {
            ExerciseItem(
                Exercise(
                    id = 1,
                    name = "Push Up",
                    description = "Push up exercise",
                ), onClick = {}, onDelete = {}
            )

        }
    }

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExerciseScreenPreview() {
    ExerciseScreen(navToExerciseEditor = {})

}

