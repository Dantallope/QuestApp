package com.example.quest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.quest.screens.QuestScreen
import com.example.quest.screens.SettingsScreen
import com.example.quest.screens.SkillsScreen
import com.example.quest.screens.StatsScreen
import com.example.quest.ui.theme.QuestTheme
import kotlinx.coroutines.delay
import java.time.LocalDate


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

    var rewardPopUpData by remember { mutableStateOf<RewardPopUpData?>(null) }
    var rerollUsed by remember { mutableStateOf(false) }
    var questPreference by remember { mutableStateOf(QuestPreference.BALANCED) }

    var totalXp by remember { mutableIntStateOf(0) }
    var strengthXp by remember { mutableIntStateOf(0) }
    var wisdomXp by remember { mutableIntStateOf(0) }

    var healthXp by remember { mutableIntStateOf(0) }
    var charismaXp by remember { mutableIntStateOf(0) }

    var skills by remember { mutableStateOf(listOf<Skill>()) }

    fun getStatLevel(statType: StatType): Int{
        return when (statType){
            StatType.STRENGTH -> LevelingSystem.levelForXp(strengthXp)
            StatType.WISDOM -> LevelingSystem.levelForXp(wisdomXp)
            StatType.HEALTH -> LevelingSystem.levelForXp(healthXp)
            StatType.CHARISMA -> LevelingSystem.levelForXp(charismaXp)
        }
    }

    fun statLabel(statType: StatType): String {
        return statType.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    fun parseSavedStatType(savedStatType: String): StatType {
        return if (savedStatType == "DISCIPLINE") {
            StatType.HEALTH
        } else {
            StatType.valueOf(savedStatType)
        }
    }

    fun parseSavedQuestPool(savedPool: String, fallbackStatType: StatType): QuestPool {
        return try {
            if (savedPool == "DISCIPLINE") {
                QuestPool.HEALTH
            } else if (savedPool.isNotEmpty()) {
                QuestPool.valueOf(savedPool)
            } else {
                QuestPool.valueOf(fallbackStatType.name)
            }
        } catch (e: Exception) {
            QuestPool.HEALTH
        }
    }

    fun parseQuestPreference(savedPreference: String): QuestPreference {
        return try {
            QuestPreference.valueOf(savedPreference)
        } catch (e: Exception) {
            QuestPreference.BALANCED
        }
    }

    fun statColor(statType: StatType): Color {
        return when (statType) {
            StatType.STRENGTH -> Color(0xFFE57373)
            StatType.WISDOM -> Color(0xFF00B4C9)
            StatType.HEALTH -> Color(0xFF00BB06)
            StatType.CHARISMA -> Color(0xFFFFC107)
        }
    }

    val sharedPreferences =
        context.getSharedPreferences("daily_challenge_prefs", Context.MODE_PRIVATE)

    fun prepareChallenge(baseChallenge: Challenge): Challenge {
        val statLevel = getStatLevel(baseChallenge.statType)
        val finalTitle = if(!baseChallenge.titleTemplate.isNullOrBlank()){
            val rawCount = baseChallenge.baseCount + ((statLevel - 1) * baseChallenge.countPerLevel)
            val count = baseChallenge.maxCount?.let { maxCount ->
                rawCount.coerceAtMost(maxCount)
            } ?: rawCount
            baseChallenge.titleTemplate.replace("{count}", count.toString())
        }else{
            baseChallenge.title
        }

        return baseChallenge.copy(title = finalTitle)
    }

    fun selectChallenge(
        availableChallenges: List<Challenge>,
        preference: QuestPreference,
        avoidTitle: String? = null
    ): Challenge? {
        if (availableChallenges.isEmpty()) return null

        val dailyPool = QuestPool.values()[LocalDate.now().dayOfYear % QuestPool.values().size]
        val preferredChallenges = if (preference == QuestPreference.BALANCED) {
            emptyList()
        } else {
            availableChallenges.filter { challenge -> challenge.statType.name == preference.name }
        }
        val poolChallenges = availableChallenges.filter { challenge -> challenge.pool == dailyPool }
        val firstPass = when {
            preferredChallenges.isNotEmpty() -> preferredChallenges
            poolChallenges.isNotEmpty() -> poolChallenges
            else -> availableChallenges
        }
        val rerollOptions = if (avoidTitle == null) {
            firstPass
        } else {
            firstPass.filter { challenge -> (challenge.titleTemplate ?: challenge.title) != avoidTitle }.ifEmpty {
                availableChallenges.filter { challenge -> (challenge.titleTemplate ?: challenge.title) != avoidTitle }
            }
        }

        return (rerollOptions.ifEmpty { firstPass }).random()
    }

    fun saveCurrentChallenge(
        challenge: Challenge,
        todayString: String
    ) {
        sharedPreferences.edit {
            putString("date", todayString)
            putString("challengeTitle", challenge.title)
            putString("challengeTitleTemplate",challenge.titleTemplate)
            putInt("challengeBaseCount",challenge.baseCount)
            putInt("challengeCountPerLevel",challenge.countPerLevel)
            putInt("challengeMaxCount", challenge.maxCount ?: 0)
            putInt("challengeXp", challenge.xp)
            putString("challengeStatType", challenge.statType.name)
            putString("challengePool", challenge.pool.name)
            putString("challengeDifficulty", challenge.difficulty.name)
            putInt("challengeMinLevel", challenge.minLevel)
            putString("status", status)
        }
    }

    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        val todayString = today.toString()

        val savedDate = sharedPreferences.getString("date", "") ?: ""
        val savedTitle = sharedPreferences.getString("challengeTitle", "") ?: ""
        val savedXp = sharedPreferences.getInt("challengeXp", 0)
        val savedStatTypeString = sharedPreferences.getString("challengeStatType", "") ?: ""
        val savedPoolString = sharedPreferences.getString("challengePool", "") ?: ""
        val savedStatus =
            sharedPreferences.getString("status", "Not completed yet") ?: "Not completed yet"
        val savedStreak = sharedPreferences.getInt("streak", 0)
        val savedLastCompletedDate = sharedPreferences.getString("lastCompletedDate", "") ?: ""
        val savedDifficultyString = sharedPreferences.getString("challengeDifficulty", "") ?:""
        val savedMinLevel = sharedPreferences.getInt("challengeMinLevel", 1)
        val savedRerollDate = sharedPreferences.getString("rerollDate", "") ?: ""
        val savedRerollUsed = sharedPreferences.getBoolean("rerollUsed", false)
        val savedQuestPreference = sharedPreferences.getString("questPreference", "") ?: ""

        val savedTotalXp = sharedPreferences.getInt("totalXp", 0)
        val savedStrengthXp = sharedPreferences.getInt("strengthXp", 0)
        val savedWisdomXp = sharedPreferences.getInt("wisdomXp", 0)
        val savedHealthXp = sharedPreferences.getInt("healthXp", 0)
        val savedCharismaXp = sharedPreferences.getInt("charismaXp", 0)

        val savedTitleTemplate = sharedPreferences.getString("challengeTitleTemplate",null)
        val savedBaseCount = sharedPreferences.getInt("challengeBaseCount",0)
        val savedCountPerLevel = sharedPreferences.getInt("challengeCountPerLevel",0)
        val savedMaxCount = sharedPreferences.getInt("challengeMaxCount",0).takeIf { it > 0 }

        skills = SkillStorage.load(sharedPreferences)
        questPreference = parseQuestPreference(savedQuestPreference)

        totalXp = savedTotalXp
        strengthXp = savedStrengthXp
        wisdomXp = savedWisdomXp
        healthXp = savedHealthXp
        charismaXp = savedCharismaXp

        val playerLevel = LevelingSystem.levelForXp(totalXp)
        val availableChallenges =
            challenges.filter { challenge -> challenge.minLevel <= playerLevel }


        val savedChallenge = if (
            savedTitle.isNotEmpty() &&
            savedStatTypeString.isNotEmpty() &&
            savedDifficultyString.isNotEmpty()
        ) {
            try {
                val savedStatType = parseSavedStatType(savedStatTypeString)
                Challenge(
                    title = savedTitle,
                    titleTemplate = savedTitleTemplate,
                    baseCount = savedBaseCount,
                    countPerLevel = savedCountPerLevel,
                    xp = savedXp,
                    statType = savedStatType,
                    pool = parseSavedQuestPool(savedPoolString, savedStatType),
                    difficulty = Difficulty.valueOf(savedDifficultyString),
                    minLevel = savedMinLevel,
                    maxCount = savedMaxCount
                )
            } catch (e: Exception) {
                null
            }
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
            currentChallenge = prepareChallenge(savedChallenge)
            status = savedStatus
            rerollUsed = savedRerollDate == todayString && savedRerollUsed
        } else {
            if (availableChallenges.isNotEmpty()) {
                val newChallenge = prepareChallenge(
                    selectChallenge(
                        availableChallenges = availableChallenges,
                        preference = questPreference
                    ) ?: availableChallenges.random()
                )
                currentChallenge = newChallenge
                status = "Not completed yet"
                rerollUsed = false

                saveCurrentChallenge(newChallenge, todayString)

                sharedPreferences.edit {
                    putString("rerollDate", todayString)
                    putBoolean("rerollUsed", false)
                }
            } else {
                //If JSON Repo is empty
                currentChallenge = Challenge(
                    title = "No Challenges Available",
                    xp = 0,
                    statType = StatType.HEALTH,
                    pool = QuestPool.HEALTH,
                    difficulty = Difficulty.EASY,
                    minLevel = 0,
                    maxCount = null
                )
                status = "Error loading challenges"
                rerollUsed = true
            }
        }
    }


    Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Quest")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedScreen == "quest",
                        onClick = { selectedScreen = "quest" },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Quest"
                            )
                        },
                        label = { Text("Quest") }
                    )
                    NavigationBarItem(
                        selected = selectedScreen == "stats",
                        onClick = { selectedScreen = "stats" },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Stats"
                            )
                        },
                        label = { Text("Stats") }
                    )
                    NavigationBarItem(
                        selected = selectedScreen == "skills",
                        onClick = { selectedScreen = "skills" },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Skills"
                            )
                        },
                        label = { Text("Skills") }
                    )
                    NavigationBarItem(
                        selected = selectedScreen == "settings",
                        onClick = { selectedScreen = "settings" },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { innerPadding ->
            when (selectedScreen) {
                "quest" -> QuestScreen(
                    innerPadding = innerPadding,
                    currentChallenge = currentChallenge,
                    status = status,
                    streak = streak,
                    totalXp = totalXp,
                    rerollUsed = rerollUsed,
                    questPreference = questPreference,
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
                                    StatType.STRENGTH -> {
                                        val oldXp = strengthXp
                                        val oldLevel = LevelingSystem.levelForXp(oldXp)
                                        strengthXp += challenge.xp
                                        val newXp = strengthXp
                                        val newLevel = LevelingSystem.levelForXp(newXp)

                                        rewardPopUpData = RewardPopUpData(
                                            statLabel = "Strength",
                                            xpGained = challenge.xp,
                                            oldXp = oldXp,
                                            newXp = newXp,
                                            oldLevel = oldLevel,
                                            newLevel = newLevel,
                                            barColor = Color(0xFFE57373)
                                        )
                                    }

                                    StatType.WISDOM -> {
                                        val oldXp = wisdomXp
                                        val oldLevel = LevelingSystem.levelForXp(oldXp)
                                        wisdomXp += challenge.xp
                                        val newXp = wisdomXp
                                        val newLevel = LevelingSystem.levelForXp(newXp)

                                        rewardPopUpData = RewardPopUpData(
                                            statLabel = "Wisdom",
                                            xpGained = challenge.xp,
                                            oldXp = oldXp,
                                            newXp = newXp,
                                            oldLevel = oldLevel,
                                            newLevel = newLevel,
                                            barColor = Color(0xFF00B4C9)
                                        )
                                    }

                                    StatType.HEALTH -> {
                                        val oldXp = healthXp
                                        val oldLevel = LevelingSystem.levelForXp(oldXp)
                                        healthXp += challenge.xp
                                        val newXp = healthXp
                                        val newLevel = LevelingSystem.levelForXp(newXp)

                                        rewardPopUpData = RewardPopUpData(
                                            statLabel = "Health",
                                            xpGained = challenge.xp,
                                            oldXp = oldXp,
                                            newXp = newXp,
                                            oldLevel = oldLevel,
                                            newLevel = newLevel,
                                            barColor = Color(0xFF00BB06)
                                        )
                                    }

                                    StatType.CHARISMA -> {
                                        val oldXp = charismaXp
                                        val oldLevel = LevelingSystem.levelForXp(oldXp)
                                        charismaXp += challenge.xp
                                        val newXp = charismaXp
                                        val newLevel = LevelingSystem.levelForXp(newXp)

                                        rewardPopUpData = RewardPopUpData(
                                            statLabel = "Charisma",
                                            xpGained = challenge.xp,
                                            oldXp = oldXp,
                                            newXp = newXp,
                                            oldLevel = oldLevel,
                                            newLevel = newLevel,
                                            barColor = Color(0xFFFFC107)
                                        )
                                    }
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
                                putInt("charismaXp", charismaXp)
                            }
                        }
                    },
                    onRerollQuest = {
                        val todayString = LocalDate.now().toString()
                        val playerLevel = LevelingSystem.levelForXp(totalXp)
                        val availableChallenges =
                            challenges.filter { challenge -> challenge.minLevel <= playerLevel }
                        val oldTitle = currentChallenge?.titleTemplate ?: currentChallenge?.title
                        val newChallenge = selectChallenge(
                            availableChallenges = availableChallenges,
                            preference = questPreference,
                            avoidTitle = oldTitle
                        )?.let { challenge -> prepareChallenge(challenge) }

                        if (newChallenge != null && status != "Completed!" && !rerollUsed) {
                            currentChallenge = newChallenge
                            status = "Not completed yet"
                            rerollUsed = true

                            saveCurrentChallenge(newChallenge, todayString)

                            sharedPreferences.edit {
                                putString("status", status)
                                putString("rerollDate", todayString)
                                putBoolean("rerollUsed", true)
                            }
                        }
                    },
                    onQuestPreferenceSelected = { preference ->
                        questPreference = preference
                        sharedPreferences.edit {
                            putString("questPreference", preference.name)
                        }
                    }
                )

                "stats" -> StatsScreen(
                    innerPadding = innerPadding,
                    totalXp = totalXp,
                    strengthXp = strengthXp,
                    wisdomXp = wisdomXp,
                    healthXp = healthXp,
                    charismaXp = charismaXp,
                    streak = streak

                )

                "settings" -> SettingsScreen(
                    innerPadding = innerPadding,
                    onUncompleteQuest = {
                        status = "Not completed yet"

                        sharedPreferences.edit {
                            putString("status", status)
                        }
                    },
                    onResetXp = {
                        totalXp = 0
                        strengthXp = 0
                        wisdomXp = 0
                        healthXp = 0
                        charismaXp = 0

                        sharedPreferences.edit{
                            putInt("totalXp",0)
                            putInt("strengthXp",0)
                            putInt("wisdomXp",0)
                            putInt("healthXp",0)
                            putInt("charismaXp",0)
                        }
                    },
                    onResetStreak = {
                        streak = 0

                        sharedPreferences.edit {
                            putInt("streak",0)
                            putString("lastCompletedDate","")
                        }
                    },
                    onClearAllData = {
                        currentChallenge = null
                        status = "Not completed yet"
                        streak = 0
                        totalXp = 0
                        strengthXp = 0
                        wisdomXp = 0
                        healthXp = 0
                        charismaXp = 0
                        skills = emptyList()
                        questPreference = QuestPreference.BALANCED
                        rerollUsed = false

                        sharedPreferences.edit {
                            clear()
                        }
                    }
                )
                "skills" -> SkillsScreen(
                    innerPadding = innerPadding,
                    skills = skills,
                    onAddSkill = {newSkill ->
                        skills = skills + newSkill
                        SkillStorage.save(sharedPreferences, skills)
                    },
                    onDeleteSkill = {skillToDelete ->
                        skills = skills.filter {it.id != skillToDelete.id}
                        SkillStorage.save(sharedPreferences, skills)
                    },
                    onCompleteSkill = { skillToComplete ->
                        val todayString = LocalDate.now().toString()

                        if (skillToComplete.lastCompletedDate != todayString) {
                            val oldXp = when (skillToComplete.statType) {
                                StatType.STRENGTH -> strengthXp
                                StatType.WISDOM -> wisdomXp
                                StatType.HEALTH -> healthXp
                                StatType.CHARISMA -> charismaXp
                            }
                            val oldLevel = LevelingSystem.levelForXp(oldXp)

                            totalXp += skillToComplete.xp

                            when (skillToComplete.statType) {
                                StatType.STRENGTH -> strengthXp += skillToComplete.xp
                                StatType.WISDOM -> wisdomXp += skillToComplete.xp
                                StatType.HEALTH -> healthXp += skillToComplete.xp
                                StatType.CHARISMA -> charismaXp += skillToComplete.xp
                            }

                            val newXp = oldXp + skillToComplete.xp
                            val newLevel = LevelingSystem.levelForXp(newXp)

                            skills = skills.map { skill ->
                                if (skill.id == skillToComplete.id) {
                                    skill.copy(lastCompletedDate = todayString)
                                } else {
                                    skill
                                }
                            }

                            SkillStorage.save(sharedPreferences, skills)

                            sharedPreferences.edit {
                                putInt("totalXp", totalXp)
                                putInt("strengthXp", strengthXp)
                                putInt("wisdomXp", wisdomXp)
                                putInt("healthXp", healthXp)
                                putInt("charismaXp", charismaXp)
                            }

                            rewardPopUpData = RewardPopUpData(
                                statLabel = statLabel(skillToComplete.statType),
                                xpGained = skillToComplete.xp,
                                oldXp = oldXp,
                                newXp = newXp,
                                oldLevel = oldLevel,
                                newLevel = newLevel,
                                barColor = statColor(skillToComplete.statType)
                            )
                        }
                    }
                )
            }
        }
    rewardPopUpData?.let { popUpData ->
        RewardCelebrationDialog(
            data = popUpData,
            onDismiss = {rewardPopUpData = null}
        )
    }
}

@Composable
fun RewardPopUp(
    data: RewardPopUpData,
    onDismiss: () -> Unit
){
    val progressAnim = remember { Animatable(LevelingSystem.progressToNextLevel(data.oldXp)) }
    var showLevelUpBanner by remember { mutableStateOf(false) }

    LaunchedEffect(data) {
        val startProgress = LevelingSystem.progressToNextLevel(data.oldXp)
        val endProgress = LevelingSystem.progressToNextLevel(data.newXp)

        progressAnim.snapTo(startProgress)

        if (data.newLevel > data.oldLevel) {
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1400)
            )

            showLevelUpBanner = true
            delay(900)

            progressAnim.snapTo(0f)

            progressAnim.animateTo(
                targetValue = endProgress,
                animationSpec = tween(durationMillis = 1200)
            )
        }else{
            progressAnim.animateTo(
                targetValue = endProgress,
                animationSpec = tween(durationMillis = 1800)
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Awesome")
            }
        },
        title = {
            Text(if(showLevelUpBanner) "LEVEL UP!" else "Quest Complete!")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("+${data.xpGained} XP to ${data.statLabel}")

                Text(
                    text = "${data.statLabel} Lv. ${data.newLevel}",
                    style = MaterialTheme.typography.titleMedium
                    )

                LinearProgressIndicator(
                    progress = { progressAnim.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(50)),
                    color = data.barColor,
                    trackColor = data.barColor.copy(alpha = 0.22f),
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )

                Text("${LevelingSystem.xpIntoCurrentLevel(data.newXp)} / ${LevelingSystem.xpNeededForNextLevel(data.newXp)} XP to next level")

                if(showLevelUpBanner){
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Level up"
                        )

                        Text(
                            text = "✨ ${data.statLabel} reached Level ${data.newLevel}! ✨",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun RewardCelebrationDialog(
    data: RewardPopUpData,
    onDismiss: () -> Unit
) {
    val progressAnim = remember { Animatable(LevelingSystem.progressToNextLevel(data.oldXp)) }
    var showLevelUpBanner by remember { mutableStateOf(false) }
    val xpIntoLevel = LevelingSystem.xpIntoCurrentLevel(data.newXp)
    val xpNeededForNextLevel = LevelingSystem.xpNeededForNextLevel(data.newXp)

    LaunchedEffect(data) {
        val endProgress = LevelingSystem.progressToNextLevel(data.newXp)

        progressAnim.snapTo(LevelingSystem.progressToNextLevel(data.oldXp))

        if (data.newLevel > data.oldLevel) {
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1200)
            )
            showLevelUpBanner = true
            delay(650)
            progressAnim.snapTo(0f)
            progressAnim.animateTo(
                targetValue = endProgress,
                animationSpec = tween(durationMillis = 900)
            )
        } else {
            progressAnim.animateTo(
                targetValue = endProgress,
                animationSpec = tween(durationMillis = 1400)
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = RoundedCornerShape(50),
                color = data.barColor.copy(alpha = 0.18f)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp),
                    tint = data.barColor
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Awesome")
            }
        },
        title = {
            Text(
                text = if (showLevelUpBanner) "Level Up!" else "Quest Complete",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+${data.xpGained} XP to ${data.statLabel}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = data.barColor
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = data.statLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Lv. ${data.newLevel}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressAnim.value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(50)),
                            color = data.barColor,
                            trackColor = data.barColor.copy(alpha = 0.22f),
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )

                        Text(
                            text = if (xpNeededForNextLevel == 0) {
                                "Max level reached"
                            } else {
                                "$xpIntoLevel / $xpNeededForNextLevel XP to next level"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                        )
                    }
                }

                if (showLevelUpBanner) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = data.barColor
                        )

                        Text(
                            text = "${data.statLabel} reached Level ${data.newLevel}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}
