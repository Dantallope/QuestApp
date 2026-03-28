package com.example.quest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Your Stats",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Track your growth and level up your character.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ){
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Level total (When apllicable)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total XP: $totalXp",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Streak: $streak days",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Attributes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatProgressRow("Strength",strengthLevel,strengthXp,strengthProgress,Color(0xFFE57373))
                StatProgressRow("Wisdom",wisdomLevel,wisdomXp,wisdomProgress,Color(0xFF00B4C9))
                StatProgressRow("Health",healthLevel,healthXp,healthProgress,Color(0xFF00BB06))
                StatProgressRow("Discipline",disciplineLevel,disciplineXp,disciplineProgress,Color(0xFF9C27B0))
                StatProgressRow("Charisma",charismaLevel,charismaXp,charismaProgress,Color(0xFFFFEB3B))
            }
        }
    }
}
@Composable
fun StatProgressRow(
    label: String,
    level: Int,
    xp: Int,
    progress: Float,
    barColor: Color
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Lv. $level",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${xp % 100} / 100 XP",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = {progress},
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
            gapSize = (0).dp,
            drawStopIndicator = {}

        )
    }
}