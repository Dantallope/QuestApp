package com.example.quest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quest.Skill
import com.example.quest.StatType

@Composable
fun SkillsScreen(
    innerPadding: PaddingValues,
    skills: List<Skill>,
    onAddSkill: (Skill) -> Unit,
    onDeleteSkill: (Skill) -> Unit
){
    var skillName by remember { mutableStateOf("") }
    var xp by remember {mutableStateOf("10")}
    var selectedStat by remember { mutableStateOf(StatType.STRENGTH) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Skills",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Create Your Own Skills",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = skillName,
            onValueChange = {skillName = it},
            label = {Text("Skill Name")},
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = xp,
            onValueChange = {xp = it},
            label = {Text("XP")},
            modifier = Modifier.fillMaxWidth()
        )

        Text("Stat Type")

        StatTypeDropdown(
            selectedStat = selectedStat,
            onStatSelected = {selectedStat = it}
        )

        Button(
            onClick = {
                val xpValue = xp.toIntOrNull() ?: 0
                if(skillName.isNotBlank() && xpValue > 0){
                    onAddSkill(
                        Skill(
                            name = skillName,
                            statType = selectedStat,
                            xp = xpValue
                        )
                    )
                    skillName = ""
                    xp = "10"
                    selectedStat = StatType.STRENGTH
                }
            }
        ){
            Text("Add Skill")
        }
        HorizontalDivider()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(skills) { skill ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(skill.name, style = MaterialTheme.typography.titleMedium)
                        Text("Stat: ${skill.statType}")
                        Text("XP: ${skill.xp}")
                        Text("Recurrence: ${skill.recurrence}")

                        Button(
                            onClick = {onDeleteSkill(skill)}
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun StatTypeDropdown(
    selectedStat: StatType,
    onStatSelected: (StatType) -> Unit
){
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }){
            Text(selectedStat.name)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            StatType.values().forEach { stat ->
                DropdownMenuItem(
                    text = {Text(stat.name)},
                    onClick = {
                        onStatSelected(stat)
                        expanded = false
                    }
                )
            }
        }
    }
}