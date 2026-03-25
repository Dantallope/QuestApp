package com.example.quest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import com.example.quest.ui.theme.QuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuestTheme() {
                QuestApp()
            }

        }
    }
}

@Composable
fun QuestApp() {
    val challenges = listOf(
        "Drink a glass of water",
        "Take a 10 minute walk",
        "Read 5 pages",
        "Do 10 push-ups",
        "Stretch for 5 minutes"
    )


    var currentChallenge by remember {
        mutableStateOf("Loading today's challenge ...")
    }

    var status by remember {
        mutableStateOf("Not completed yet")
    }

    var streak by remember{
        mutableStateOf(0)
    }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("daily_challenge_prefs", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        val todayString = today.toString()

        val savedDate = sharedPreferences.getString("date", "") ?:""
        val savedChallenge = sharedPreferences.getString("challenge", "") ?: ""
        val savedStatus = sharedPreferences.getString("status", "Not completed yet") ?: "Not completed yet"
        val savedStreak = sharedPreferences.getInt("streak",0)
        val savedLastCompletedDate = sharedPreferences.getString("lastCompletedDate","") ?:""

        streak = savedStreak

        val lastCompletedDate = if (savedLastCompletedDate.isNotEmpty()){
            LocalDate.parse(savedLastCompletedDate)
        }else{
            null
        }

        if (lastCompletedDate != null && lastCompletedDate.isBefore(today.minusDays(1)) && savedStatus != "Completed!"){
            streak = 0
            sharedPreferences.edit {
                putInt("streak", streak)
            }
        }

        if (savedDate == todayString && savedChallenge.isNotEmpty()){
            currentChallenge = savedChallenge
            status = savedStatus
        }else{
            val newChallenge = challenges.random()
            currentChallenge = newChallenge
            status = "Not completed yet"

            sharedPreferences.edit {
                putString("date", todayString)
                    .putString("challenge", newChallenge)
                    .putString("status", status)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Daily Challenge",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
            )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Today's Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentChallenge,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Current Streak",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "\uD83D\uDD25 $streak days",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val today = LocalDate.now()
            val todayString = today.toString()
            val savedLastCompletedDate = sharedPreferences.getString("lastCompletedDate", "") ?:""

            if (status != "Completed!"){
                if (savedLastCompletedDate.isEmpty()){
                    streak = 1
                }else{
                    val lastCompletedDate = LocalDate.parse(savedLastCompletedDate)

                    streak = when{
                        lastCompletedDate == today -> streak
                        lastCompletedDate == today.minusDays(1) -> streak + 1
                        else -> 1
                    }
                }
                status = "Completed!"

                sharedPreferences.edit {
                    putString("status", status)
                        .putInt("streak", streak)
                        .putString("lastCompletedDate", todayString)
                }
            }

        },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Mark Complete",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            status = "Not completed"
            sharedPreferences.edit {
                putString("status", status)
            }
        }) {
            Text("Mark Not Complete")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Status: $status")
        Text("Current streak: $streak")
    }
}
