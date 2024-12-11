package com.example.fitnessapp.ui.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.Exercise


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEditorScreen(
    exerciseId: Int?,
    popBackStack: () -> Unit,
    onSave: () -> Unit,
    viewModel: ExerciseEditorViewModel = viewModel(
        factory = ExerciseEditorViewModel.Factory(
            LocalContext.current
        )
    )
) {

    LaunchedEffect(exerciseId) {
        if (exerciseId != null) {
            viewModel.getExercise(exerciseId)
        }
    }

    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.title_edit_exercise)) },
                navigationIcon = {
                    IconButton(onClick = { popBackStack() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.btn_cancel)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveExercise(exerciseId, onSave) }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(R.string.btn_save)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text(stringResource(R.string.label_exercise_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description ?: "",
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text(stringResource(R.string.label_exercise_description)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                singleLine = false
            )
        }
    }

}