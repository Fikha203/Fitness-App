package com.example.fitnessapp.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.R
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navToHistoryDetail: (Int) -> Unit,
    viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.Factory(LocalContext.current)
    )
) {
    val historyList by viewModel.historyList.collectAsState()
    val planeNameMap by viewModel.planNameMap.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "History")
                }
            )
        },
    ) { innerPadding ->


        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(historyList) { history ->
                val planName = planeNameMap[history.planId] ?: "Unknown Plan"
                HistoryItem(
                    historyDate = history.date,
                    planName = planName,
                    onHistoryClick = { navToHistoryDetail(history.id) },
                    onDelete = { viewModel.deleteHistory(history) }
                )
            }
        }
    }
}

@Composable
fun HistoryItem(historyDate: Long, planName: String, onHistoryClick: () -> Unit, onDelete: () -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RectangleShape,
        onClick = onHistoryClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),

        ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = planName,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Box {
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(historyDate),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryItemPreview() {
    HistoryItem(historyDate = 1633660800000, planName = "Push Day", onHistoryClick = {}, onDelete = {})
}