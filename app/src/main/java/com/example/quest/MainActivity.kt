package com.example.quest

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

    var currentChallenge by remember {
        mutableStateOf("Press the button to get today's challenge")
    }

    var status by remember {
        mutableStateOf("Not completed yet")
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
            currentChallenge = challenges.random()
            status = "Not completed yet"
        }) {
            Text("Get Random Challenge")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {status = "Completed!"}) {
            Text("Mark Complete")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {status = "Not Completed"}) {
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