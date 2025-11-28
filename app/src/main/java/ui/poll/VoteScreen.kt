package com.example.myapplication.ui.poll

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.viewmodel.PollOptionsState
import com.example.myapplication.ui.viewmodel.PollViewModel
import com.example.myapplication.ui.viewmodel.PollState

@Composable
fun VoteScreen(
    pollId: String,
    viewModel: PollViewModel = viewModel(),
    onVoteComplete: () -> Unit,
) {
    val pollState by viewModel.pollState.collectAsState()
    val optionsState by viewModel.optionsState.collectAsState()

    var selectedSingle by remember { mutableStateOf<String?>(null) }
    val selectedMultiple = remember { mutableStateListOf<String>() }

    LaunchedEffect(pollId) {
        viewModel.loadPolls()
        viewModel.loadOptions(pollId)
    }

    val pollInfo = (pollState as? PollState.Success)?.polls?.find { it.id == pollId }
    val isMultipleChoice = pollInfo?.is_multiple_choice ?: false
    val isAnonymous = pollInfo?.is_anonymous ?: false

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Text(
            text = pollInfo?.title ?: "투표",
            style = MaterialTheme.typography.headlineSmall
        )

        if (isAnonymous) {
            Text(
                "익명 투표입니다.",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        when (optionsState) {
            is PollOptionsState.Loading -> CircularProgressIndicator()

            is PollOptionsState.Error ->
                Text("옵션 불러오기 실패", color = MaterialTheme.colorScheme.error)

            is PollOptionsState.Success -> {
                val options = (optionsState as PollOptionsState.Success).options

                options.forEach { option ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isMultipleChoice) {
                            Checkbox(
                                checked = selectedMultiple.contains(option.id),
                                onCheckedChange = { checked ->
                                    if (checked) selectedMultiple.add(option.id)
                                    else selectedMultiple.remove(option.id)
                                }
                            )
                        } else {
                            RadioButton(
                                selected = selectedSingle == option.id,
                                onClick = { selectedSingle = option.id }
                            )
                        }

                        Text(option.option_text)
                    }
                }

                Spacer(Modifier.height(20.dp))

                val canVote =
                    (isMultipleChoice && selectedMultiple.isNotEmpty()) ||
                            (!isMultipleChoice && selectedSingle != null)

                Button(
                    onClick = {
                        val optionIds =
                            if (isMultipleChoice) selectedMultiple.toList()
                            else listOfNotNull(selectedSingle)

                        viewModel.vote(pollId, optionIds) {
                            onVoteComplete()
                        }
                    },
                    enabled = canVote,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("투표하기")
                }
            }
            else -> {}
        }
    }
}
