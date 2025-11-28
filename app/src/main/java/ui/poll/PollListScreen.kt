package com.example.myapplication.ui.poll

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.viewmodel.PollState
import com.example.myapplication.ui.viewmodel.PollViewModel
import java.net.URLEncoder

@Composable
fun PollListScreen(
    navController: NavController,
    viewModel: PollViewModel
) {
    val pollState by viewModel.pollState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPolls()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var targetPollId by remember { mutableStateOf("") }

    when (val state = pollState) {
        is PollState.Idle -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
            }
        }

        is PollState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is PollState.Error -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { Text(state.message, color = MaterialTheme.colorScheme.error) }

        is PollState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.polls) { poll ->

                    val encodedTitle = URLEncoder.encode(poll.title, "UTF-8")

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            navController.navigate(
                                                "pollDetail/${poll.id}/$encodedTitle"
                                            )
                                        }
                                ) {
                                    Text(poll.title, fontWeight = FontWeight.Bold)
                                    Text(
                                        poll.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(
                                    text = "삭제",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .clickable {
                                            targetPollId = poll.id
                                            showDeleteDialog = true
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("투표 삭제") },
            text = { Text("정말 이 투표를 삭제할까요?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePoll(
                            pollId = targetPollId,
                            onSuccess = {
                                viewModel.loadPolls()
                            },
                            onError = { println(it) }
                        )
                    }
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}
