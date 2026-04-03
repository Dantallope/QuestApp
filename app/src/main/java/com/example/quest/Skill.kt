package com.example.quest

data class Skill(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val statType: StatType,
    val xp: Int,
    val recurrence: String = "DAILY"
)