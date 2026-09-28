package com.example.ui.isometric

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

data class IsoPoint3D(val x: Float, val y: Float, val z: Float) {
    fun toScreen(centerX: Float, centerY: Float, tileSize: Float, zoom: Float): Offset {
        val rad30 = 0.5235987756f // 30 degrees in radians
        val cos30 = cos(rad30)
        val sin30 = sin(rad30)

        val screenX = centerX + (x - y) * cos30 * tileSize * zoom
        val screenY = centerY + (x + y) * sin30 * (tileSize * 0.58f) * zoom - (z * tileSize * 0.7f * zoom)
        return Offset(screenX, screenY)
    }
}

enum class WorldStage(
    val stageNumber: Int,
    val title: String,
    val yorubaName: String,
    val dayRange: IntRange,
    val description: String,
    val primaryPigment: Color
) {
    THE_COT(
        stageNumber = 1,
        title = "The Cot",
        yorubaName = "Ibùsùn",
        dayRange = 1..30,
        description = "A solitary simple cot in an empty void. White sketchbook paper, clean lines, and a quiet beginning.",
        primaryPigment = Color(0xFF6C757D)
    ),
    THE_HEARTH(
        stageNumber = 2,
        title = "The Hearth & Frame",
        yorubaName = "Àdòró & Igi",
        dayRange = 31..90,
        description = "Timber frames rise from the paper. Stone hearth sparks with embers and the first sprouts unfurl.",
        primaryPigment = Color(0xFFE76F51)
    ),
    THE_GARDEN(
        stageNumber = 3,
        title = "The Garden",
        yorubaName = "Ọgbà Ọlọ́ràá",
        dayRange = 91..180,
        description = "Lush vegetable beds, fresh well water, fruit blossoms, and a cozy cottage sheltered under trees.",
        primaryPigment = Color(0xFF52B788)
    ),
    THE_HAMLET(
        stageNumber = 4,
        title = "The Hamlet & Bridge",
        yorubaName = "Abúlé & Afárá",
        dayRange = 181..270,
        description = "Multiple workshops, flowing canal with an arched stone bridge, glowing lanterns, and woven community paths.",
        primaryPigment = Color(0xFF48CAE4)
    ),
    THE_CITY(
        stageNumber = 5,
        title = "The City",
        yorubaName = "Ìlú Nlá",
        dayRange = 271..365,
        description = "A thriving small metropolis with clocktower spires, park fountains, terraced roofs, and vibrant 365-day memories.",
        primaryPigment = Color(0xFFE9C46A)
    );

    companion object {
        fun fromDay(day: Int): WorldStage {
            return when {
                day <= 30 -> THE_COT
                day <= 90 -> THE_HEARTH
                day <= 180 -> THE_GARDEN
                day <= 270 -> THE_HAMLET
                else -> THE_CITY
            }
        }
    }
}
