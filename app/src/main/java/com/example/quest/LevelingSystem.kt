package com.example.quest

import kotlin.math.floor
import kotlin.math.pow

object LevelingSystem {
    const val MaxLevel = 99

    fun levelForXp(xp: Int): Int {
        val safeXp = xp.coerceAtLeast(0)
        for (level in 1 until MaxLevel) {
            if (safeXp < xpForLevel(level + 1)) {
                return level
            }
        }
        return MaxLevel
    }

    fun xpForLevel(level: Int): Int {
        val targetLevel = level.coerceIn(1, MaxLevel)
        var points = 0

        for (currentLevel in 1 until targetLevel) {
            points += floor(currentLevel + 300 * 2.0.pow(currentLevel / 7.0)).toInt()
        }

        return points / 4
    }

    fun xpIntoCurrentLevel(xp: Int): Int {
        val level = levelForXp(xp)
        return (xp - xpForLevel(level)).coerceAtLeast(0)
    }

    fun xpNeededForNextLevel(xp: Int): Int {
        val level = levelForXp(xp)
        if (level >= MaxLevel) return 0
        return xpForLevel(level + 1) - xpForLevel(level)
    }

    fun progressToNextLevel(xp: Int): Float {
        val needed = xpNeededForNextLevel(xp)
        if (needed <= 0) return 1f
        return xpIntoCurrentLevel(xp) / needed.toFloat()
    }
}
