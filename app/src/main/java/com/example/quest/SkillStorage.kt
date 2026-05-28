package com.example.quest

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object SkillStorage {
    private const val SkillsKey = "skills"

    fun load(sharedPreferences: SharedPreferences): List<Skill> {
        val savedSkills = sharedPreferences.getString(SkillsKey, null) ?: return emptyList()
        return try {
            val skillArray = JSONArray(savedSkills)
            List(skillArray.length()) { index ->
                val skillObject = skillArray.getJSONObject(index)
                Skill(
                    id = skillObject.getLong("id"),
                    name = skillObject.getString("name"),
                    statType = StatType.valueOf(skillObject.getString("statType")),
                    xp = skillObject.getInt("xp"),
                    recurrence = skillObject.optString("recurrence", "DAILY"),
                    lastCompletedDate = skillObject.optString("lastCompletedDate")
                        .takeIf { it.isNotBlank() }
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(
        sharedPreferences: SharedPreferences,
        skills: List<Skill>
    ) {
        val skillArray = JSONArray()
        skills.forEach { skill ->
            skillArray.put(
                JSONObject()
                    .put("id", skill.id)
                    .put("name", skill.name)
                    .put("statType", skill.statType.name)
                    .put("xp", skill.xp)
                    .put("recurrence", skill.recurrence)
                    .put("lastCompletedDate", skill.lastCompletedDate ?: "")
            )
        }

        sharedPreferences.edit()
            .putString(SkillsKey, skillArray.toString())
            .apply()
    }
}
