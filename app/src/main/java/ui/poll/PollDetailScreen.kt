package com.example.myapplication.ui.poll

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.myapplication.ui.viewmodel.PollOptionsState
import com.example.myapplication.ui.viewmodel.PollViewModel
import com.example.myapplication.ui.viewmodel.PollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollDetailScreen(
    navController: NavController,
    pollId: String,
    title: String,
    viewModel: PollViewModel,
    onVoteSuccess: () -> Unit
) {
    val optionsState by viewModel.optionsState.collectAsState()
    val pollsState by viewModel.pollState.collectAsState()

    var selectedSingle by remember { mutableStateOf<String?>(null) }
    val selectedMultiple = remember { mutableStateListOf<String>() }

    LaunchedEffect(pollId) {
        viewModel.loadOptions(pollId)
        viewModel.loadPolls()
    }

    val pollInfo = (pollsState as? PollState.Success)
        ?.polls
        ?.find { it.id == pollId }

    val isMultipleChoice = pollInfo?.is_multiple_choice ?: false
    val isAnonymous = pollInfo?.is_anonymous ?: false

    Column(modifier = Modifier.padding(16.dp)) {

        Text(text = title, style = MaterialTheme.typography.headlineSmall)

        if (isAnonymous) {
            Text("익명 투표입니다.", color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = optionsState) {

            is PollOptionsState.Success -> {
                state.options.forEach { option ->

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

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val optionIds =
                            if (isMultipleChoice) selectedMultiple.toList()
                            else listOfNotNull(selectedSingle)

                        viewModel.vote(pollId, optionIds) {

                            navController.navigate("pollResult/$pollId/$isAnonymous")

                            onVoteSuccess()
                        }
                    },
                    enabled = (isMultipleChoice && selectedMultiple.isNotEmpty())
                            || (!isMultipleChoice && selectedSingle != null),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("투표하기")
                }
            }

            is PollOptionsState.Loading -> CircularProgressIndicator()
            is PollOptionsState.Error -> Text(state.message)
            else -> {}
        }
    }
}
