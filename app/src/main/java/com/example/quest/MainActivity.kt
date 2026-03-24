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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuestApp()
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
//For the old button setup
//    var currentChallenge by remember {
//        mutableStateOf("Press the button to get today's challenge")
//    }

    var currentChallenge by remember {
        mutableStateOf("Loading today's challenge ...")
    }

    var status by remember {
        mutableStateOf("Not completed yet")
    }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("daily_challenge_prefs", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        val today = LocalDate.now().toString()

        val savedDate = sharedPreferences.getString("date", "") ?:""
        val savedChallenge = sharedPreferences.getString("challenge", "") ?: ""
        val savedSatus = sharedPreferences.getString("status", "Not completed yet") ?: "Not completed yet"

        if (savedDate == today && savedChallenge.isNotEmpty()){
            currentChallenge = savedChallenge
            status = savedSatus
        }else{
            val newChallenge = challenges.random()
            currentChallenge = newChallenge
            status = "Not completed yet"

            sharedPreferences.edit()
                .putString("date",today)
                .putString("challenge",newChallenge)
                .putString("status",status)
                .apply()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Daily Challenge")

        Spacer(modifier = Modifier.height(16.dp))

        Text(currentChallenge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            status = "Completed!"
            sharedPreferences.edit()
                .putString("status",status)
                .apply()
        }) {
            Text("Mark Complete")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            status = "Not completed"
            sharedPreferences.edit()
                .putString("status",status)
                .apply()
        }) {
            Text("Mark Not Complete")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Status: $status")
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    QuestTheme {
//        Greeting("Android")
//    }
//}