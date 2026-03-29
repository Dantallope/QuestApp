package com.example.quest


import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.quest.screens.QuestScreen
import com.example.quest.screens.StatsScreen
import com.example.quest.screens.SettingsScreen
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import com.example.quest.ui.theme.QuestTheme
import kotlinx.coroutines.launch
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestApp() {
    var selectedScreen by remember { mutableStateOf("quest") }
    val context = LocalContext.current
    val challenges = remember {
        ChallengeRepo.loadChallenges(context)
    }


    var currentChallenge by remember {
        mutableStateOf<Challenge?>(null)
    }

    var status by remember {
        mutableStateOf("Not completed yet")
    }

    var streak by remember {
        mutableIntStateOf(0)
    }
    var totalXp by remember { mutableIntStateOf(0) }
    var strengthXp by remember { mutableIntStateOf(0) }
    var wisdomXp by remember { mutableIntStateOf(0) }
    var healthXp by remember { mutableIntStateOf(0) }
    var disciplineXp by remember { mutableIntStateOf(0) }
    var charismaXp by remember { mutableIntStateOf(0) }

    val sharedPreferences =
        context.getSharedPreferences("daily_challenge_prefs", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        val todayString = today.toString()

        val savedDate = sharedPreferences.getString("date", "") ?: ""
        val savedTitle = sharedPreferences.getString("challengeTitle", "") ?: ""
        val savedXp = sharedPreferences.getInt("challengeXp", 0)
        val savedStatTypeString = sharedPreferences.getString("challengeStatType", "") ?: ""
        val savedStatus =
            sharedPreferences.getString("status", "Not completed yet") ?: "Not completed yet"
        val savedStreak = sharedPreferences.getInt("streak", 0)
        val savedLastCompletedDate = sharedPreferences.getString("lastCompletedDate", "") ?: ""

        val savedTotalXp = sharedPreferences.getInt("totalXp", 0)
        val savedStrengthXp = sharedPreferences.getInt("strengthXp", 0)
        val savedWisdomXp = sharedPreferences.getInt("wisdomXp", 0)
        val savedHealthXp = sharedPreferences.getInt("healthXp", 0)
        val savedDisciplineXp = sharedPreferences.getInt("disciplineXp", 0)
        val savedCharismaXp = sharedPreferences.getInt("charismaXp", 0)

        totalXp = savedTotalXp
        strengthXp = savedStrengthXp
        wisdomXp = savedWisdomXp
        healthXp = savedHealthXp
        disciplineXp = savedDisciplineXp
        charismaXp = savedCharismaXp

        val savedChallenge = if (savedTitle.isNotEmpty()) {
            Challenge(
                title = savedTitle,
                xp = savedXp,
                statType = StatType.valueOf(savedStatTypeString)
            )
        } else {
            null
        }

        streak = savedStreak

        val lastCompletedDate = if (savedLastCompletedDate.isNotEmpty()) {
            LocalDate.parse(savedLastCompletedDate)
        } else {
            null
        }

        if (lastCompletedDate != null && lastCompletedDate.isBefore(today.minusDays(1)) && savedStatus != "Completed!") {
            streak = 0
            sharedPreferences.edit {
                putInt("streak", streak)
            }
        }

        if (savedDate == todayString && savedChallenge != null) {
            currentChallenge = savedChallenge
            status = savedStatus
        } else {
            val newChallenge = challenges.random()
            currentChallenge = newChallenge
            status = "Not completed yet"

            sharedPreferences.edit {
                putString("date", todayString)
                putString("challengeTitle", newChallenge.title)
                putInt("challengeXp", newChallenge.xp)
                putString("challengeStatType", newChallenge.statType.name)
                putString("status", status)
            }
        }
    }


        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        text = "Quest Menu",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = true,
                        onClick = {
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Stats") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("Quest")
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open menu"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                bottomBar = {
                    NavigationBar{
                        NavigationBarItem(
                            selected = selectedScreen == "quest",
                            onClick = {selectedScreen = "quest"},
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Quest"
                                )
                            },
                            label = {Text("Quest")}
                        )
                        NavigationBarItem(
                            selected = selectedScreen == "stats",
                            onClick = {selectedScreen = "stats"},
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Stats"
                                )
                            },
                            label = {Text("Stats")}
                        )
                        NavigationBarItem(
                            selected = selectedScreen == "settings",
                            onClick = {selectedScreen = "settings"},
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = {Text("settings")}
                        )
                    }
                }
            ) { innerPadding ->
                when(selectedScreen){
                    "quest" -> QuestScreen(
                        innerPadding = innerPadding,
                        currentChallenge = currentChallenge,
                        status = status,
                        streak = streak,
                        totalXp = totalXp,
                        onMarkComplete = {
                            val today = LocalDate.now()
                            val todayString = today.toString()
                            val savedLastCompletedDate =
                                sharedPreferences.getString("lastCompletedDate", "") ?: ""

                            val challenge = currentChallenge

                            if (status != "Completed!") {
                                if (challenge != null) {
                                    totalXp += challenge.xp

                                    when (challenge.statType) {
                                        StatType.STRENGTH -> strengthXp += challenge.xp
                                        StatType.WISDOM -> wisdomXp += challenge.xp
                                        StatType.HEALTH -> healthXp += challenge.xp
                                        StatType.DISCIPLINE -> disciplineXp += challenge.xp
                                        StatType.CHARISMA -> charismaXp += challenge.xp
                                    }
                                }

                                if (savedLastCompletedDate.isEmpty()) {
                                    streak = 1
                                } else {
                                    val lastCompletedDate = LocalDate.parse(savedLastCompletedDate)

                                    streak = when {
                                        lastCompletedDate == today -> streak
                                        lastCompletedDate == today.minusDays(1) -> streak + 1
                                        else -> 1
                                    }
                                }

                                status = "Completed!"

                                sharedPreferences.edit {
                                    putString("status", status)
                                    putInt("streak", streak)
                                    putString("lastCompletedDate", todayString)

                                    putInt("totalXp", totalXp)
                                    putInt("strengthXp", strengthXp)
                                    putInt("wisdomXp", wisdomXp)
                                    putInt("healthXp", healthXp)
                                    putInt("disciplineXp", disciplineXp)
                                    putInt("charismaXp", charismaXp)
                                }
                            }
                        }
                    )

                    "stats" -> StatsScreen(
                        innerPadding = innerPadding,
                        totalXp = totalXp,
                        strengthXp = strengthXp,
                        wisdomXp = wisdomXp,
                        healthXp = healthXp,
                        disciplineXp = disciplineXp,
                        charismaXp = charismaXp,
                        streak = streak

                    )

                    "settings" -> SettingsScreen(
                    )
                }
                }
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(MaterialTheme.colorScheme.background)
//                        .padding(innerPadding)
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Daily Challenge",
//                        style = MaterialTheme.typography.headlineMedium,
//                        color = MaterialTheme.colorScheme.onBackground
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = CardDefaults.cardColors(
//                            containerColor = MaterialTheme.colorScheme.surface
//                        ),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(20.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                text = "Today's Challenge",
//                                style = MaterialTheme.typography.titleMedium,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//                            Spacer(modifier = Modifier.height(12.dp))
//
//                            Text(
//                                text = currentChallenge?.title ?: "Loading today's quest",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//
//                            Spacer(modifier = Modifier.height(8.dp))
//
//                            Text(
//                                text = "Reward: ${currentChallenge?.xp ?: 0} XP",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//                            Text(
//                                text = "Stat: ${currentChallenge?.statType?.name ?: "NONE"}",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(
//                            text = "Status",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                        Text(
//                            text = status,
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        Text(
//                            text = "Current Streak",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                        Text(
//                            text = "\uD83D\uDD25 $streak days",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        Text(
//                            text = "Total XP",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                        Text(
//                            text = "$totalXp XP",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        Text("Strength: $strengthXp", color = MaterialTheme.colorScheme.onSurface)
//                        Text("Wisdom: $wisdomXp", color = MaterialTheme.colorScheme.onSurface)
//                        Text("Health: $healthXp", color = MaterialTheme.colorScheme.onSurface)
//                        Text(
//                            "Discipline: $disciplineXp",
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                        Text("Charisma: $charismaXp", color = MaterialTheme.colorScheme.onSurface)
//                    }
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Button(
//                        onClick = {
//                            val today = LocalDate.now()
//                            val todayString = today.toString()
//                            val savedLastCompletedDate =
//                                sharedPreferences.getString("lastCompletedDate", "") ?: ""
//                            val challenge = currentChallenge
//                            if (status != "Completed!") {
//                                if (challenge != null) {
//                                    totalXp += challenge.xp
//
//                                    when (challenge.statType) {
//                                        StatType.STRENGTH -> strengthXp += challenge.xp
//                                        StatType.WISDOM -> wisdomXp += challenge.xp
//                                        StatType.HEALTH -> healthXp += challenge.xp
//                                        StatType.DISCIPLINE -> disciplineXp += challenge.xp
//                                        StatType.CHARISMA -> charismaXp += challenge.xp
//                                    }
//                                }
//                                if (savedLastCompletedDate.isEmpty()) {
//                                    streak = 1
//                                } else {
//                                    val lastCompletedDate = LocalDate.parse(savedLastCompletedDate)
//
//                                    streak = when {
//                                        lastCompletedDate == today -> streak
//                                        lastCompletedDate == today.minusDays(1) -> streak + 1
//                                        else -> 1
//                                    }
//                                }
//                                status = "Completed!"
//
//                                sharedPreferences.edit {
//                                    putString("status", status)
//                                    putInt("streak", streak)
//                                    putString("lastCompletedDate", todayString)
//
//                                    putInt("totalXp", totalXp)
//                                    putInt("strengthXp", strengthXp)
//                                    putInt("wisdomXp", wisdomXp)
//                                    putInt("healthXp", healthXp)
//                                    putInt("disciplineXp", disciplineXp)
//                                    putInt("charismaXp", charismaXp)
//                                }
//                            }
//                        },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.primary,
//                            contentColor = MaterialTheme.colorScheme.onPrimary
//                        )
//                    ) {
//                        Text(
//                            text = "Mark Complete",
//                            color = MaterialTheme.colorScheme.onPrimary
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(12.dp))
//
//
//                }
            }
        }
