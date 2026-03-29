package com.example.quest

data class Challenge(
    val title: String,
    val xp:Int,
    val statType: StatType,
    val difficulty: Difficulty,
    val minLevel: Int
)