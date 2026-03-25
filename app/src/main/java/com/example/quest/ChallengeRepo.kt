package com.example.quest

object ChallengeRepo{
    val challenges = listOf(
        Challenge("Drink a glass of water",5, StatType.HEALTH),
        Challenge("Take a 10 minute walk",10,StatType.HEALTH),
        Challenge("Read 5 pages",10,StatType.WISDOM),
        Challenge("Do 10 push-ups", 15, StatType.STRENGTH),
        Challenge("Stretch for 5 minuites",8,StatType.DISCIPLINE),
        Challenge("Compliment someone", 7, StatType.CHARISMA)
    )
}