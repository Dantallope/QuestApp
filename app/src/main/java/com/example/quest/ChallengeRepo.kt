package com.example.quest

import android.content.Context
import org.json.JSONArray
object ChallengeRepo{
    fun loadChallenges(context: Context): List<Challenge>{
        val jsonString = context.assets
            .open("challenges.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(jsonString)
        val challenges = mutableListOf<Challenge>()

        for (i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)

            val title = jsonObject.getString("title")
            val xp = jsonObject.getInt("xp")
            val statTypeString = jsonObject.getString("statType")

            val challenge = Challenge(
                title = title,
                xp = xp,
                statType = StatType.valueOf(statTypeString)
            )
            challenges.add(challenge)
        }
        return challenges
    }
}