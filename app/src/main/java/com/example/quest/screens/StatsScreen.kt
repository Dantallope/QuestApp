package com.example.quest.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme

@Composable
fun StatsScreen(
    innerPadding: PaddingValues,
    totalXp: Int,
    strengthXp: Int,
    wisdomXp: Int,
    healthXp: Int,
    disciplineXp: Int,
    charismaXp: Int,
    streak: Int
){
    val strengthLevel = (strengthXp / 100) + 1
    val wisdomLevel = (wisdomXp / 100) + 1
    val healthLevel = (healthXp / 100) + 1
    val disciplineLevel = (disciplineXp / 100) + 1
    val charismaLevel = (charismaXp / 100) + 1

    val strengthProgress = (strengthXp % 100) / 100f
    val wisdomProgress = (wisdomXp % 100) / 100f
    val healthProgress = (healthXp % 100) / 100f
    val disciplineProgress = (disciplineXp % 100) / 100f
    val charismaProgress = (charismaXp % 100) / 100f
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {

        Text("Stats")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Total Xp: $totalXp")
        Text("🔥 Streak: $streak")

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "💪 Strength: $strengthLevel",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "$strengthXp xp",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = {strengthProgress},
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "🧠 Wisdom: $wisdomXp",
            style = MaterialTheme.typography.titleMedium
            )
        Text(
            text = "$wisdomXp xp",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = {wisdomProgress},
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "❤️ Health: $healthXp xp",
            style = MaterialTheme.typography.titleMedium
            )
        Text(
            text = "$healthXp",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = {healthProgress},
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "🧘 Discipline: $disciplineXp",
            style = MaterialTheme.typography.titleMedium
            )
        Text(
            text = "$disciplineXp xp",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = {disciplineProgress},
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "✨ Charisma: $charismaXp",
            style = MaterialTheme.typography.titleMedium
            )
        Text(
            text = "$charismaXp xp",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = {charismaProgress},
            modifier = Modifier.fillMaxWidth()
        )
    }
}