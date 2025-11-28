package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapplication.ui.LoginScreen
import com.example.myapplication.ui.poll.PollListScreen
import com.example.myapplication.ui.poll.PollDetailScreen
import com.example.myapplication.ui.poll.PollResultScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.PollViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.viewmodel.ResultViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val pollViewModel: PollViewModel = viewModel()
                Scaffold { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "welcome",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)   // ← 핵심!

               ) {

                    // -------------------------------
                    // Welcome Screen
                    // -------------------------------
                    composable("welcome") {
                        WelcomeCardLayout(
                            onLoginClick = { navController.navigate("login") },
                            onExitClick = { finish() }
                        )
                    }

                    // -------------------------------
                    // Login Screen
                    // -------------------------------
                    composable("login") {
                        LoginScreen(
                            onSignupSuccess = {
                                navController.navigate("pollList") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onLoginSuccess = {
                                navController.navigate("pollList") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // -------------------------------
                    // Poll List
                    // -------------------------------
                        composable("pollList") {
                            PollListScreen(
                                navController = navController,
                                viewModel = pollViewModel
                            )
                        }

                    // -------------------------------
                    // Poll Detail
                    // -------------------------------
                    composable(
                        route = "pollDetail/{pollId}/{title}",
                        arguments = listOf(
                            navArgument("pollId") { type = NavType.StringType },
                            navArgument("title") { type = NavType.StringType }
                        )
                    ) { entry ->
                        val pollId = entry.arguments!!.getString("pollId")!!
                        val title = entry.arguments!!.getString("title")!!

                        PollDetailScreen(
                            navController = navController,
                            pollId = pollId,
                            title = title,
                            viewModel = pollViewModel,
                            onVoteSuccess = {
                                navController.navigate("pollResult/$pollId")
                            }
                        )
                    }

                    // -------------------------------
                    // Poll Result
                    // -------------------------------
                        composable(
                            route = "pollResult/{pollId}/{isAnonymous}",
                            arguments = listOf(
                                navArgument("pollId") { type = NavType.StringType },
                                navArgument("isAnonymous") { type = NavType.BoolType }
                            )
                        ) { entry ->
                            val pollId = entry.arguments!!.getString("pollId")!!
                            val isAnonymous = entry.arguments!!.getBoolean("isAnonymous")

                            val resultViewModel: ResultViewModel = viewModel()

                            PollResultScreen(
                                navController = navController,
                                pollId = pollId,
                                isAnonymous = isAnonymous,
                                viewModel = resultViewModel
                            )
                        }



                }
            }
        }
    }
}



@Composable
fun WelcomeCardLayout(
    onLoginClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔥 텍스트 한 줄 + 폰트 크게
            Text(
                text = "Freshman Project Selection App",
                style = typography.headlineLarge.copy(           // headlineMedium → headlineLarge
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp                            // 크기 확실히 증가
                ),
                color = Color(0xFFFF9800),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 🔥 SPARCS 로고 더 크게
                    Image(
                        painter = painterResource(id = R.drawable.sparcs_logo),
                        contentDescription = "SPARCS Logo",
                        modifier = Modifier
                            .fillMaxWidth(0.7f)   // 기존 0.5f → 0.7f 로 확대
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = "SPARCS 투표에 오신 것을\n환영합니다!",
                        style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                        color = colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onLoginClick,
                shape = shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Log in", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onExitClick,
                shape = shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Exit", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "made by jj",
                style = typography.bodyMedium.copy(
                    fontSize = 20.sp,           // 🔥 기존보다 조금 더 크게
                    color = colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
}

