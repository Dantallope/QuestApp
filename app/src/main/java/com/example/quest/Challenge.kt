package com.example.quest

data class Challenge(
    val title: String,
    val titleTemplate: String? = null,
    val xp:Int,
    val statType: StatType,
    val difficulty: Difficulty,
    val minLevel: Int,
    val baseCount: Int = 0,
    val countPerLevel: Int = 0
)