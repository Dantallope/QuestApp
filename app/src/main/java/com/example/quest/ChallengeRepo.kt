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
            val titleTemplate = if(jsonObject.has("titleTemplate")) jsonObject.getString("titleTemplate")else null
            val baseCount = if(jsonObject.has("baseCount")) jsonObject.getInt("baseCount") else 0
            val countPerLevel = if(jsonObject.has("countPerLevel")) jsonObject.getInt("countPerLevel") else 0
            val xp = jsonObject.getInt("xp")
            val statTypeString = jsonObject.getString("statType")
            val difficultyString = jsonObject.getString("difficulty")
            val minLevel = jsonObject.getInt("minLevel")

            val challenge = Challenge(
                title = title,
                titleTemplate = titleTemplate,
                baseCount = baseCount,
                countPerLevel = countPerLevel,
                xp = xp,
                statType = StatType.valueOf(statTypeString),
                difficulty = Difficulty.valueOf(difficultyString),
                minLevel = minLevel
            )
            challenges.add(challenge)
        }
        return challenges
    }
}