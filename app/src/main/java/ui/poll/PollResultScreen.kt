package com.example.myapplication.ui.poll

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.viewmodel.ResultViewModel
import com.example.myapplication.data.poll.ResultState
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun PollResultScreen(
    navController: NavController,
    pollId: String,
    isAnonymous: Boolean,
    viewModel: ResultViewModel = viewModel()
) {
    val state by viewModel.resultState.collectAsState()

    LaunchedEffect(key1 = pollId, isAnonymous) {
        viewModel.loadResults(pollId, isAnonymous)
    }

    when (state) {

        is ResultState.Loading -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Success -> {
            val s = state as ResultState.Success
            val totalVotes = s.results.values.sumOf { it.size }

            Column(Modifier.padding(20.dp)) {

                Text("📋 투표 결과", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(20.dp))

                // 옵션별 상세 박스
                s.results.forEach { (optionId, userList) ->
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("옵션: ${s.optionMap[optionId]}")
                            Text("투표 수: ${userList.size}")

                            if (s.isAnonymous) {
                                Text("투표자 목록: 익명 투표입니다.")
                            } else {
                                Text(
                                    "투표자 목록: " +
                                            userList.joinToString {
                                                s.userMap[it] ?: "??"
                                            }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(16.dp))
                Text("통계 (총 ${totalVotes}명 참여)")
                Spacer(Modifier.height(30.dp))

                if (totalVotes > 0) {
                    val values = s.results.values.map { it.size.toFloat() / totalVotes }

                    val labels = s.results.keys.map { optionId ->
                        s.optionMap[optionId] ?: ""
                    }

                    val percentages = s.results.values.map { list ->
                        if (totalVotes == 0) 0 else (list.size * 100 / totalVotes)
                    }

                    val colors = listOf(
                        Color(0xFFE57373),
                        Color(0xFF64B5F6),
                        Color(0xFF81C784),
                        Color(0xFFFFB74D),
                        Color(0xFF9575CD)
                    )

                    PieChartWithLabels(
                        values = values,
                        labels = labels,
                        colors = colors,
                        percentages = percentages,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        is ResultState.Error -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("결과 불러오기 실패")
            }
        }

        ResultState.Idle -> Unit
    }
}


@Composable
fun PieChartWithLabels(
    values: List<Float>,
    labels: List<String>,
    colors: List<Color>,
    percentages: List<Int>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            var startAngle = -90f

            values.forEachIndexed { index, value ->

                val sweep = value * 360f

                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )

                val middleAngle = startAngle + sweep / 2f
                val rad = Math.toRadians(middleAngle.toDouble())

                val radius = size.minDimension * 0.22f

                val x = center.x + (radius * kotlin.math.cos(rad)).toFloat()
                val y = center.y + (radius * kotlin.math.sin(rad)).toFloat()

                val text = "${labels[index]} ${percentages[index]}%"

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        text,
                        x,
                        y,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 35f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }

                startAngle += sweep
            }
        }
    }
}
