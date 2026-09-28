package com.example.ui.isometric

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun IsometricSketchbookCanvas(
    dayNumber: Int,
    saturation: Float,
    bloomTrigger: Boolean,
    onBloomFinished: () -> Unit = {},
    onElementSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // Pan and zoom states
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    // Ambient life animations: wind sway, water ripples, drifting petals, lantern glow
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_life")
    val ambientPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_phase"
    )

    val windSway = sin(ambientPhase) * 2.2f
    val waterGleam = sin(ambientPhase * 2f) * 0.5f + 0.5f
    val lanternFlicker = sin(ambientPhase * 3.5f) * 0.15f + 0.85f

    // Bloom burst animation
    val bloomAnim = remember { Animatable(0f) }

    LaunchedEffect(bloomTrigger) {
        if (bloomTrigger) {
            bloomAnim.snapTo(0f)
            bloomAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200, easing = LinearEasing)
            )
            onBloomFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    panOffset += pan
                    zoomScale = (zoomScale * zoom).coerceIn(0.55f, 2.5f)
                }
            }
            .pointerInput(dayNumber) {
                detectTapGestures {
                    val stage = WorldStage.fromDay(dayNumber)
                    onElementSelected(stage.title)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2f + panOffset.x
            val centerY = canvasHeight / 2f + panOffset.y + 40f
            val tileSize = 28f * zoomScale

            val clampedSat = saturation.coerceIn(0f, 1f)

            // 1. Draw Sketchbook Paper Background & Guide Grid
            drawSketchbookPaper(
                width = canvasWidth,
                height = canvasHeight,
                centerX = centerX,
                centerY = centerY,
                tileSize = tileSize,
                isDark = isDark
            )

            // 2. Draw World Ground Platform with Realistic Layering and Path
            drawWorldPlatform(
                centerX = centerX,
                centerY = centerY,
                tileSize = tileSize,
                dayNumber = dayNumber,
                saturation = clampedSat,
                isDark = isDark
            )

            // 3. Draw The Cot (Day 1+) with Realistic Soft Shadow and Wood Planks
            drawTheCot(
                centerX = centerX,
                centerY = centerY,
                tileSize = tileSize,
                saturation = clampedSat,
                isDark = isDark,
                windSway = windSway
            )

            // 4. Draw The Hearth & Cabin Frame (Day 31+) with Animated Curling Smoke and Embers
            if (dayNumber >= 31) {
                drawTheHearthAndFrame(
                    centerX = centerX,
                    centerY = centerY,
                    tileSize = tileSize,
                    dayNumber = dayNumber,
                    saturation = clampedSat,
                    isDark = isDark,
                    ambientPhase = ambientPhase
                )
            }

            // 5. Draw The Garden & Well (Day 91+) with Wind-Swaying Leaves and Rippling Water
            if (dayNumber >= 91) {
                drawTheGardenAndWell(
                    centerX = centerX,
                    centerY = centerY,
                    tileSize = tileSize,
                    dayNumber = dayNumber,
                    saturation = clampedSat,
                    isDark = isDark,
                    windSway = windSway,
                    waterGleam = waterGleam
                )
            }

            // 6. Draw The Hamlet & Canal (Day 181+) with Animated Water Currents and Glowing Lanterns
            if (dayNumber >= 181) {
                drawTheHamletAndCanal(
                    centerX = centerX,
                    centerY = centerY,
                    tileSize = tileSize,
                    dayNumber = dayNumber,
                    saturation = clampedSat,
                    isDark = isDark,
                    ambientPhase = ambientPhase,
                    lanternFlicker = lanternFlicker
                )
            }

            // 7. Draw The City & Clocktower Spire (Day 271+) with Tiered Fountain Reflections
            if (dayNumber >= 271) {
                drawTheCityAndClocktower(
                    centerX = centerX,
                    centerY = centerY,
                    tileSize = tileSize,
                    dayNumber = dayNumber,
                    saturation = clampedSat,
                    isDark = isDark,
                    waterGleam = waterGleam
                )
            }

            // 8. Subtle Floating Breeze Petals (Realism & Motion)
            drawAmbientDriftingPetals(
                centerX = centerX,
                centerY = centerY,
                tileSize = tileSize,
                ambientPhase = ambientPhase,
                saturation = clampedSat
            )

            // 9. Draw Daily Bloom ceremony animation
            if (bloomAnim.value > 0f && bloomAnim.value < 1f) {
                drawBloomCeremony(
                    centerX = centerX,
                    centerY = centerY,
                    tileSize = tileSize,
                    progress = bloomAnim.value
                )
            }

            // 10. Blueprint Framing & Scale Stamps
            drawSketchbookAnnotations(
                dayNumber = dayNumber,
                saturation = clampedSat,
                width = canvasWidth,
                height = canvasHeight,
                isDark = isDark
            )
        }
    }
}

private fun isoToScreen(
    x: Float,
    y: Float,
    z: Float,
    centerX: Float,
    centerY: Float,
    tileSize: Float
): Offset {
    val cos30 = 0.8660254f
    val sin30 = 0.5f

    val screenX = centerX + (x - y) * cos30 * tileSize
    val screenY = centerY + (x + y) * sin30 * (tileSize * 0.58f) - (z * tileSize * 0.72f)
    return Offset(screenX, screenY)
}

private fun satColor(greyscale: Color, saturated: Color, saturation: Float): Color {
    val sat = saturation.coerceIn(0f, 1f)
    return Color(
        red = greyscale.red + (saturated.red - greyscale.red) * sat,
        green = greyscale.green + (saturated.green - greyscale.green) * sat,
        blue = greyscale.blue + (saturated.blue - greyscale.blue) * sat,
        alpha = greyscale.alpha + (saturated.alpha - greyscale.alpha) * sat
    )
}

// 1. Sketchbook Paper Background & Dotted Grid
private fun DrawScope.drawSketchbookPaper(
    width: Float,
    height: Float,
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    isDark: Boolean
) {
    val paperBg = if (isDark) Color(0xFF14171C) else Color(0xFFFAF8F3)
    val dotColor = if (isDark) Color(0xFF262C38) else Color(0xFFE2DDD4)

    drawRect(color = paperBg, size = Size(width, height))

    val spacing = tileSize * 1.5f
    val startX = (centerX % (spacing * 1.732f)) - spacing * 2
    val startY = (centerY % spacing) - spacing * 2

    var y = startY
    while (y < height + spacing * 2) {
        var x = startX
        while (x < width + spacing * 2) {
            drawCircle(color = dotColor, radius = 1.2f, center = Offset(x, y))
            x += spacing * 1.732f
        }
        y += spacing
    }
}

// 2. World Ground Platform (Terraced Paper/Grass Island)
private fun DrawScope.drawWorldPlatform(
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    dayNumber: Int,
    saturation: Float,
    isDark: Boolean
) {
    val radius = when {
        dayNumber <= 30 -> 2.6f
        dayNumber <= 90 -> 4.2f
        dayNumber <= 180 -> 5.8f
        dayNumber <= 270 -> 7.0f
        else -> 8.2f
    }

    val pTop = isoToScreen(0f, -radius, 0f, centerX, centerY, tileSize)
    val pRight = isoToScreen(radius, 0f, 0f, centerX, centerY, tileSize)
    val pBottom = isoToScreen(0f, radius, 0f, centerX, centerY, tileSize)
    val pLeft = isoToScreen(-radius, 0f, 0f, centerX, centerY, tileSize)

    val thickness = 1.3f
    val pBottomDrop = isoToScreen(0f, radius, -thickness, centerX, centerY, tileSize)
    val pLeftDrop = isoToScreen(-radius, 0f, -thickness, centerX, centerY, tileSize)
    val pRightDrop = isoToScreen(radius, 0f, -thickness, centerX, centerY, tileSize)

    // Directional Shading: Left face in warm reflected light, Right face in shadow
    val leftFaceColor = satColor(
        greyscale = if (isDark) Color(0xFF181B22) else Color(0xFFD6D1C6),
        saturated = Color(0xFFB5A998),
        saturation = saturation
    )
    val rightFaceColor = satColor(
        greyscale = if (isDark) Color(0xFF11141A) else Color(0xFFC2BCB0),
        saturated = Color(0xFF9E9281),
        saturation = saturation
    )
    val topColor = satColor(
        greyscale = if (isDark) Color(0xFF1E232B) else Color(0xFFEDE9E0),
        saturated = if (dayNumber <= 30) Color(0xFFEBE7DE)
                    else if (dayNumber <= 90) Color(0xFFE2ECD8)
                    else Color(0xFFD8EBCD),
        saturation = saturation
    )

    val outlineColor = if (isDark) Color(0xFF38404E) else Color(0xFF4A453E)

    // Left skirt
    val leftSkirt = Path().apply {
        moveTo(pLeft.x, pLeft.y)
        lineTo(pBottom.x, pBottom.y)
        lineTo(pBottomDrop.x, pBottomDrop.y)
        lineTo(pLeftDrop.x, pLeftDrop.y)
        close()
    }
    drawPath(leftSkirt, color = leftFaceColor)
    drawPath(leftSkirt, color = outlineColor, style = Stroke(width = 1.5f))

    // Right skirt
    val rightSkirt = Path().apply {
        moveTo(pBottom.x, pBottom.y)
        lineTo(pRight.x, pRight.y)
        lineTo(pRightDrop.x, pRightDrop.y)
        lineTo(pBottomDrop.x, pBottomDrop.y)
        close()
    }
    drawPath(rightSkirt, color = rightFaceColor)
    drawPath(rightSkirt, color = outlineColor, style = Stroke(width = 1.5f))

    // Top surface
    val topPath = Path().apply {
        moveTo(pTop.x, pTop.y)
        lineTo(pRight.x, pRight.y)
        lineTo(pBottom.x, pBottom.y)
        lineTo(pLeft.x, pLeft.y)
        close()
    }
    drawPath(topPath, color = topColor)
    drawPath(topPath, color = outlineColor, style = Stroke(width = 2.0f, join = StrokeJoin.Round))

    // Cobblestone path winding from center towards garden and bridge
    if (dayNumber >= 31) {
        val pathStoneColor = satColor(
            greyscale = if (isDark) Color(0xFF282D36) else Color(0xFFDCD7CC),
            saturated = Color(0xFFC7C2B6),
            saturation = saturation
        )
        val pathSteps = listOf(
            Pair(0.5f, 0.5f), Pair(1.2f, 0.4f), Pair(1.9f, 0.6f),
            Pair(2.6f, 0.3f), Pair(3.2f, -0.2f), Pair(3.8f, -1.0f)
        )
        for ((sx, sy) in pathSteps) {
            val stepScreen = isoToScreen(sx, sy, 0.02f, centerX, centerY, tileSize)
            drawCircle(pathStoneColor, radius = 5.5f, center = stepScreen)
            drawCircle(outlineColor, radius = 5.5f, center = stepScreen, style = Stroke(width = 0.8f))
        }
    }
}

// 3. The Cot (Day 1 Milestone) with Realistic Wood Texture and Soft Shadows
private fun DrawScope.drawTheCot(
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    saturation: Float,
    isDark: Boolean,
    windSway: Float
) {
    val cotX = 0f
    val cotY = 0f
    val cotW = 1.5f
    val cotL = 2.3f
    val legH = 0.5f
    val mattressH = 0.38f

    // Soft drop shadow underneath the cot
    val shadowColor = if (isDark) Color(0x66000000) else Color(0x33000000)
    val s0 = isoToScreen(cotX - cotW / 2 + 0.2f, cotY - cotL / 2 + 0.3f, 0f, centerX, centerY, tileSize)
    val s1 = isoToScreen(cotX + cotW / 2 + 0.3f, cotY - cotL / 2 + 0.2f, 0f, centerX, centerY, tileSize)
    val s2 = isoToScreen(cotX + cotW / 2 + 0.3f, cotY + cotL / 2 + 0.3f, 0f, centerX, centerY, tileSize)
    val s3 = isoToScreen(cotX - cotW / 2 + 0.2f, cotY + cotL / 2 + 0.3f, 0f, centerX, centerY, tileSize)

    val shadowPath = Path().apply {
        moveTo(s0.x, s0.y)
        lineTo(s1.x, s1.y)
        lineTo(s2.x, s2.y)
        lineTo(s3.x, s3.y)
        close()
    }
    drawPath(shadowPath, color = shadowColor)

    val woodColor = satColor(
        greyscale = if (isDark) Color(0xFF3A3E47) else Color(0xFF7D776C),
        saturated = Color(0xFF8B5A2B),
        saturation = saturation
    )
    val linenColor = satColor(
        greyscale = if (isDark) Color(0xFF282D36) else Color(0xFFF6F4EE),
        saturated = Color(0xFFFDFBF7),
        saturation = saturation
    )
    val quiltColor = satColor(
        greyscale = if (isDark) Color(0xFF22262E) else Color(0xFFDFDBD1),
        saturated = Color(0xFF3D5A80),
        saturation = saturation
    )
    val outlineColor = if (isDark) Color(0xFF485160) else Color(0xFF2B2823)

    // 4 Legs
    val corners = listOf(
        Pair(cotX - cotW / 2, cotY - cotL / 2),
        Pair(cotX + cotW / 2, cotY - cotL / 2),
        Pair(cotX + cotW / 2, cotY + cotL / 2),
        Pair(cotX - cotW / 2, cotY + cotL / 2)
    )
    for ((cx, cy) in corners) {
        val foot = isoToScreen(cx, cy, 0f, centerX, centerY, tileSize)
        val topLeg = isoToScreen(cx, cy, legH, centerX, centerY, tileSize)
        drawLine(woodColor, foot, topLeg, strokeWidth = 3.5f, cap = StrokeCap.Round)
        drawLine(outlineColor, foot, topLeg, strokeWidth = 1.0f)
    }

    // Wooden Frame Box
    val f0 = isoToScreen(cotX - cotW / 2, cotY - cotL / 2, legH, centerX, centerY, tileSize)
    val f1 = isoToScreen(cotX + cotW / 2, cotY - cotL / 2, legH, centerX, centerY, tileSize)
    val f2 = isoToScreen(cotX + cotW / 2, cotY + cotL / 2, legH, centerX, centerY, tileSize)
    val f3 = isoToScreen(cotX - cotW / 2, cotY + cotL / 2, legH, centerX, centerY, tileSize)

    val framePath = Path().apply {
        moveTo(f0.x, f0.y)
        lineTo(f1.x, f1.y)
        lineTo(f2.x, f2.y)
        lineTo(f3.x, f3.y)
        close()
    }
    drawPath(framePath, color = woodColor)
    drawPath(framePath, color = outlineColor, style = Stroke(width = 1.2f))

    // Mattress layer with realistic linen folds
    val mZ = legH + mattressH
    val m2 = isoToScreen(cotX + cotW / 2, cotY + cotL / 2, mZ, centerX, centerY, tileSize)
    val m3 = isoToScreen(cotX - cotW / 2, cotY + cotL / 2, mZ, centerX, centerY, tileSize)

    val matFront = Path().apply {
        moveTo(f3.x, f3.y)
        lineTo(f2.x, f2.y)
        lineTo(m2.x, m2.y)
        lineTo(m3.x, m3.y)
        close()
    }
    drawPath(matFront, color = linenColor)
    drawPath(matFront, color = outlineColor, style = Stroke(width = 1.2f))

    val m0 = isoToScreen(cotX - cotW / 2, cotY - cotL / 2, mZ, centerX, centerY, tileSize)
    val m1 = isoToScreen(cotX + cotW / 2, cotY - cotL / 2, mZ, centerX, centerY, tileSize)
    val matTop = Path().apply {
        moveTo(m0.x, m0.y)
        lineTo(m1.x, m1.y)
        lineTo(m2.x, m2.y)
        lineTo(m3.x, m3.y)
        close()
    }
    drawPath(matTop, color = linenColor)
    drawPath(matTop, color = outlineColor, style = Stroke(width = 1.4f))

    // Quilt Blanket at foot
    val qStartL = cotY
    val q0 = isoToScreen(cotX - cotW / 2, qStartL, mZ + 0.05f, centerX, centerY, tileSize)
    val q1 = isoToScreen(cotX + cotW / 2, qStartL, mZ + 0.05f, centerX, centerY, tileSize)
    val q2 = isoToScreen(cotX + cotW / 2, cotY + cotL / 2, mZ + 0.05f, centerX, centerY, tileSize)
    val q3 = isoToScreen(cotX - cotW / 2, cotY + cotL / 2, mZ + 0.05f, centerX, centerY, tileSize)

    val quiltPath = Path().apply {
        moveTo(q0.x, q0.y)
        lineTo(q1.x, q1.y)
        lineTo(q2.x, q2.y)
        lineTo(q3.x, q3.y)
        close()
    }
    drawPath(quiltPath, color = quiltColor)
    drawPath(quiltPath, color = outlineColor, style = Stroke(width = 1.2f))

    // Beside stool with candle
    val stoolPos = isoToScreen(cotX + cotW / 2 + 0.7f, cotY - 0.2f, 0f, centerX, centerY, tileSize)
    val stoolTop = isoToScreen(cotX + cotW / 2 + 0.7f, cotY - 0.2f, 0.42f, centerX, centerY, tileSize)
    drawLine(woodColor, stoolPos, stoolTop, strokeWidth = 3f)
    drawCircle(woodColor, radius = 5.5f, center = stoolTop)

    // Animated candle flame
    val flamePos = isoToScreen(cotX + cotW / 2 + 0.7f, cotY - 0.2f, 0.68f, centerX, centerY, tileSize)
    val flameColor = satColor(Color(0xFF88847C), Color(0xFFF4A261), saturation)
    drawCircle(flameColor, radius = 3.6f + sin(windSway) * 0.4f, center = flamePos)

    // Sprout swaying slightly in the breeze
    val sproutOrigin = isoToScreen(cotX - cotW / 2 - 0.5f, cotY + 0.5f, 0f, centerX, centerY, tileSize)
    val sproutTop = isoToScreen(
        cotX - cotW / 2 - 0.5f + (windSway * 0.05f),
        cotY + 0.5f,
        0.38f,
        centerX,
        centerY,
        tileSize
    )
    val stemColor = satColor(Color(0xFF555960), Color(0xFF2D6A4F), saturation)
    val leafColor = satColor(Color(0xFF7A808A), Color(0xFF52B788), saturation)

    drawLine(stemColor, sproutOrigin, sproutTop, strokeWidth = 2.4f, cap = StrokeCap.Round)
    drawCircle(leafColor, radius = 4f, center = sproutTop)
}

// 4. The Hearth & Timber Frame (Day 31+) with Animated Smoke
private fun DrawScope.drawTheHearthAndFrame(
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    dayNumber: Int,
    saturation: Float,
    isDark: Boolean,
    ambientPhase: Float
) {
    val hX = -1.8f
    val hY = -1.8f
    val hH = 2.3f

    val stoneColor = satColor(
        greyscale = if (isDark) Color(0xFF2A2E36) else Color(0xFF8E8B83),
        saturated = Color(0xFF6B705C),
        saturation = saturation
    )
    val emberColor = satColor(Color(0xFF77736A), Color(0xFFE76F51), saturation)
    val outlineColor = if (isDark) Color(0xFF485160) else Color(0xFF2B2823)

    // Hearth chimney block
    val base = isoToScreen(hX, hY, 0f, centerX, centerY, tileSize)
    val chimneyTop = isoToScreen(hX, hY, hH, centerX, centerY, tileSize)
    drawRect(stoneColor, topLeft = Offset(base.x - 13f, chimneyTop.y), size = Size(26f, base.y - chimneyTop.y))
    drawRect(outlineColor, topLeft = Offset(base.x - 13f, chimneyTop.y), size = Size(26f, base.y - chimneyTop.y), style = Stroke(width = 1.3f))

    // Flickering ember glow inside hearth
    val emberFlicker = (sin(ambientPhase * 4f) * 0.3f + 0.7f)
    drawCircle(emberColor.copy(alpha = emberFlicker), radius = 6f, center = Offset(base.x, base.y - 8f))

    // Animated rising smoke puffs
    val smokeColor = if (isDark) Color(0x559AA0B0) else Color(0x446B655B)
    for (i in 0..2) {
        val phase = (ambientPhase + i * 2.1f) % 6.28318f
        val fraction = phase / 6.28318f
        val smokeX = base.x + sin(phase * 2f) * 6f + (i * 2f)
        val smokeY = chimneyTop.y - (fraction * 28f)
        val smokeRadius = 3.5f + fraction * 5f
        drawCircle(smokeColor.copy(alpha = (1f - fraction) * 0.5f), radius = smokeRadius, center = Offset(smokeX, smokeY))
    }

    // Timber posts & beams
    val timberColor = satColor(
        greyscale = if (isDark) Color(0xFF383D48) else Color(0xFF7B6D5D),
        saturated = Color(0xFFA67C52),
        saturation = saturation
    )

    val posts = listOf(
        Pair(-2.2f, -2.2f), Pair(2.2f, -2.2f),
        Pair(2.2f, 2.2f), Pair(-2.2f, 2.2f)
    )
    val postHeight = 2.4f
    for ((px, py) in posts) {
        val pBase = isoToScreen(px, py, 0f, centerX, centerY, tileSize)
        val pTop = isoToScreen(px, py, postHeight, centerX, centerY, tileSize)
        drawLine(timberColor, pBase, pTop, strokeWidth = 3.8f, cap = StrokeCap.Round)
        drawLine(outlineColor, pBase, pTop, strokeWidth = 1.0f)
    }

    for (i in posts.indices) {
        val next = (i + 1) % posts.size
        val bStart = isoToScreen(posts[i].first, posts[i].second, postHeight, centerX, centerY, tileSize)
        val bEnd = isoToScreen(posts[next].first, posts[next].second, postHeight, centerX, centerY, tileSize)
        drawLine(timberColor, bStart, bEnd, strokeWidth = 3f)
    }
}

// 5. The Garden & Well (Day 91+ / Month 3) with Swaying Trees and Water Gleam
private fun DrawScope.drawTheGardenAndWell(
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    dayNumber: Int,
    saturation: Float,
    isDark: Boolean,
    windSway: Float,
    waterGleam: Float
) {
    val outlineColor = if (isDark) Color(0xFF485160) else Color(0xFF2B2823)

    // Full Cozy Cottage Roof
    val roofRidgeStart = isoToScreen(0f, -2.4f, 3.8f, centerX, centerY, tileSize)
    val roofRidgeEnd = isoToScreen(0f, 2.4f, 3.8f, centerX, centerY, tileSize)
    val eaveL0 = isoToScreen(-2.6f, -2.4f, 2.3f, centerX, centerY, tileSize)
    val eaveL1 = isoToScreen(-2.6f, 2.4f, 2.3f, centerX, centerY, tileSize)
    val eaveR0 = isoToScreen(2.6f, -2.4f, 2.3f, centerX, centerY, tileSize)
    val eaveR1 = isoToScreen(2.6f, 2.4f, 2.3f, centerX, centerY, tileSize)

    val roofColorL = satColor(
        greyscale = if (isDark) Color(0xFF252932) else Color(0xFF8A8275),
        saturated = Color(0xFFE76F51),
        saturation = saturation
    )
    val roofColorR = satColor(
        greyscale = if (isDark) Color(0xFF1D212A) else Color(0xFF756D60),
        saturated = Color(0xFFD05338),
        saturation = saturation
    )

    val roofLeft = Path().apply {
        moveTo(roofRidgeStart.x, roofRidgeStart.y)
        lineTo(roofRidgeEnd.x, roofRidgeEnd.y)
        lineTo(eaveL1.x, eaveL1.y)
        lineTo(eaveL0.x, eaveL0.y)
        close()
    }
    drawPath(roofLeft, color = roofColorL)
    drawPath(roofLeft, color = outlineColor, style = Stroke(width = 1.4f))

    val roofRight = Path().apply {
        moveTo(roofRidgeStart.x, roofRidgeStart.y)
        lineTo(roofRidgeEnd.x, roofRidgeEnd.y)
        lineTo(eaveR1.x, eaveR1.y)
        lineTo(eaveR0.x, eaveR0.y)
        close()
    }
    drawPath(roofRight, color = roofColorR)
    drawPath(roofRight, color = outlineColor, style = Stroke(width = 1.4f))

    // Dormer window with warm glowing light
    val dormerPos = isoToScreen(1.3f, 0f, 2.8f, centerX, centerY, tileSize)
    val windowGlow = satColor(Color(0xFF7F7A70), Color(0xFFFFD166), saturation)
    drawCircle(windowGlow, radius = 6.5f, center = dormerPos)
    drawCircle(outlineColor, radius = 6.5f, center = dormerPos, style = Stroke(width = 1.2f))

    // Stone Wishing Well at (3.8, -2.5) with Animated Water Surface
    val wellTop = isoToScreen(3.8f, -2.5f, 0.6f, centerX, centerY, tileSize)
    val stoneWellColor = satColor(Color(0xFF333842), Color(0xFF6C757D), saturation)
    val wellWaterColor = satColor(Color(0xFF222830), Color(0xFF48CAE4), saturation)

    drawCircle(stoneWellColor, radius = 10f, center = wellTop)
    drawCircle(wellWaterColor.copy(alpha = 0.8f + waterGleam * 0.2f), radius = 7.5f, center = wellTop)
    drawCircle(outlineColor, radius = 10f, center = wellTop, style = Stroke(width = 1.2f))

    // Well canopy
    val canopyPos = isoToScreen(3.8f, -2.5f, 1.2f, centerX, centerY, tileSize)
    drawLine(stoneWellColor, Offset(wellTop.x - 7f, wellTop.y), Offset(canopyPos.x - 7f, canopyPos.y), strokeWidth = 2f)
    drawLine(stoneWellColor, Offset(wellTop.x + 7f, wellTop.y), Offset(canopyPos.x + 7f, canopyPos.y), strokeWidth = 2f)
    drawLine(roofColorL, Offset(canopyPos.x - 12f, canopyPos.y), Offset(canopyPos.x + 12f, canopyPos.y), strokeWidth = 3.2f)

    // Raised Garden Beds with rows of vegetables
    val gardenBedColor = satColor(Color(0xFF2E2620), Color(0xFF7F4F24), saturation)
    val leafGreen = satColor(Color(0xFF384035), Color(0xFF52B788), saturation)
    val carrotOrange = satColor(Color(0xFF4A443A), Color(0xFFF77F00), saturation)

    val g0 = isoToScreen(-4.4f, 1.8f, 0f, centerX, centerY, tileSize)
    val g1 = isoToScreen(-2.4f, 1.8f, 0f, centerX, centerY, tileSize)
    val g2 = isoToScreen(-2.4f, 3.8f, 0f, centerX, centerY, tileSize)
    val g3 = isoToScreen(-4.4f, 3.8f, 0f, centerX, centerY, tileSize)

    val bedPath = Path().apply {
        moveTo(g0.x, g0.y)
        lineTo(g1.x, g1.y)
        lineTo(g2.x, g2.y)
        lineTo(g3.x, g3.y)
        close()
    }
    drawPath(bedPath, color = gardenBedColor)
    drawPath(bedPath, color = outlineColor, style = Stroke(width = 1.2f))

    val vegPoints = listOf(
        Pair(-3.8f, 2.3f), Pair(-3.3f, 2.3f), Pair(-2.8f, 2.3f),
        Pair(-3.8f, 3.2f), Pair(-3.3f, 3.2f), Pair(-2.8f, 3.2f)
    )
    for ((vx, vy) in vegPoints) {
        val vScreen = isoToScreen(vx, vy, 0.1f, centerX, centerY, tileSize)
        drawCircle(leafGreen, radius = 5.5f, center = vScreen)
        drawCircle(carrotOrange, radius = 2.2f, center = Offset(vScreen.x, vScreen.y + 1f))
    }

    // Blooming Fruit Tree (Wind Swaying with Shimmering Blossoms)
    val treeBase = isoToScreen(2.8f, 3.5f, 0f, centerX, centerY, tileSize)
    val crownXOffset = windSway * 1.5f
    val treeCrown = isoToScreen(2.8f, 3.5f, 1.8f, centerX, centerY, tileSize)

    val trunkColor = satColor(Color(0xFF574E45), Color(0xFF6F4E37), saturation)
    val foliageColor = satColor(Color(0xFF6B7268), Color(0xFF40916C), saturation)
    val blossomColor = satColor(Color(0xFFD6CBC7), Color(0xFFF4978E), saturation)

    drawLine(trunkColor, treeBase, Offset(treeCrown.x + crownXOffset * 0.5f, treeCrown.y), strokeWidth = 5.5f, cap = StrokeCap.Round)
    drawCircle(foliageColor, radius = 17f, center = Offset(treeCrown.x + crownXOffset, treeCrown.y))
    drawCircle(outlineColor, radius = 17f, center = Offset(treeCrown.x + crownXOffset, treeCrown.y), style = Stroke(width = 1.2f))

    // Blossoms on the tree
    val blossomOffsets = listOf(
        Offset(-6f, -4f), Offset(5f, -6f), Offset(0f, 5f), Offset(-7f, 4f), Offset(7f, 2f)
    )
    for (bo in blossomOffsets) {
        drawCircle(blossomColor, radius = 3.4f, center = Offset(treeCrown.x + crownXOffset + bo.x, treeCrown.y + bo.y))
    }
}

// 6. The Hamlet & Canal (Day 181+) with Flowing Current Lines and Glowing Streetlamps
private fun DrawScope.drawTheHamletAndCanal(
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    dayNumber: Int,
    saturation: Float,
    isDark: Boolean,
    ambientPhase: Float,
    lanternFlicker: Float
) {
    val outlineColor = if (isDark) Color(0xFF485160) else Color(0xFF2B2823)

    // Flowing Canal
    val canalW = 1.4f
    val canalX0 = 4.8f
    val canalX1 = canalX0 + canalW

    val cTop0 = isoToScreen(canalX0, -6.5f, -0.2f, centerX, centerY, tileSize)
    val cTop1 = isoToScreen(canalX1, -6.5f, -0.2f, centerX, centerY, tileSize)
    val cBot1 = isoToScreen(canalX1, 6.5f, -0.2f, centerX, centerY, tileSize)
    val cBot0 = isoToScreen(canalX0, 6.5f, -0.2f, centerX, centerY, tileSize)

    val canalWaterColor = satColor(Color(0xFF1E2833), Color(0xFF0077B6), saturation)

    val canalPath = Path().apply {
        moveTo(cTop0.x, cTop0.y)
        lineTo(cTop1.x, cTop1.y)
        lineTo(cBot1.x, cBot1.y)
        lineTo(cBot0.x, cBot0.y)
        close()
    }
    drawPath(canalPath, color = canalWaterColor)
    drawPath(canalPath, color = outlineColor, style = Stroke(width = 1.0f))

    // Subtle Animated Water Flow Specular Lines along canal
    val waterShineColor = Color.White.copy(alpha = 0.25f)
    for (i in -4..4) {
        val yPos = ((ambientPhase * 1.2f + i * 1.5f) % 12f) - 6f
        val wStart = isoToScreen(canalX0 + 0.2f, yPos, -0.18f, centerX, centerY, tileSize)
        val wEnd = isoToScreen(canalX1 - 0.2f, yPos, -0.18f, centerX, centerY, tileSize)
        drawLine(waterShineColor, wStart, wEnd, strokeWidth = 1.0f)
    }

    // Arched Stone Bridge
    val bridgeStart = isoToScreen(canalX0 - 0.3f, 0f, 0.2f, centerX, centerY, tileSize)
    val bridgeApex = isoToScreen(5.5f, 0f, 0.85f, centerX, centerY, tileSize)
    val bridgeEnd = isoToScreen(canalX1 + 0.3f, 0f, 0.2f, centerX, centerY, tileSize)

    val bridgeColor = satColor(Color(0xFF323844), Color(0xFFADB5BD), saturation)
    val archPath = Path().apply {
        moveTo(bridgeStart.x, bridgeStart.y)
        quadraticTo(bridgeApex.x, bridgeApex.y, bridgeEnd.x, bridgeEnd.y)
        lineTo(bridgeEnd.x, bridgeEnd.y + 7f)
        quadraticTo(bridgeApex.x, bridgeApex.y + 7f, bridgeStart.x, bridgeStart.y + 7f)
        close()
    }
    drawPath(archPath, color = bridgeColor)
    drawPath(archPath, color = outlineColor, style = Stroke(width = 1.5f))

    // Secondary Artisan Cottage
    val artX = 6.6f
    val artY = 1.5f
    val artBase = isoToScreen(artX, artY, 0f, centerX, centerY, tileSize)
    val artRoof = isoToScreen(artX, artY, 2.0f, centerX, centerY, tileSize)

    val artWallColor = satColor(Color(0xFF282E39), Color(0xFFE9D8A6), saturation)
    val artRoofColor = satColor(Color(0xFF1E232C), Color(0xFF005F73), saturation)

    drawRect(artWallColor, topLeft = Offset(artBase.x - 14f, artRoof.y), size = Size(28f, artBase.y - artRoof.y))
    drawRect(outlineColor, topLeft = Offset(artBase.x - 14f, artRoof.y), size = Size(28f, artBase.y - artRoof.y), style = Stroke(width = 1.2f))

    val artRoofPath = Path().apply {
        moveTo(artRoof.x - 18f, artRoof.y)
        lineTo(artRoof.x, artRoof.y - 12f)
        lineTo(artRoof.x + 18f, artRoof.y)
        close()
    }
    drawPath(artRoofPath, color = artRoofColor)
    drawPath(artRoofPath, color = outlineColor, style = Stroke(width = 1.2f))

    // Warm Flickering Streetlamps
    val lamps = listOf(Pair(4.2f, -1.8f), Pair(4.2f, 1.8f))
    val lanternGlow = satColor(Color(0xFF6A655C), Color(0xFFFFB703), saturation)
    for ((lx, ly) in lamps) {
        val lBase = isoToScreen(lx, ly, 0f, centerX, centerY, tileSize)
        val lTop = isoToScreen(lx, ly, 1.3f, centerX, centerY, tileSize)
        drawLine(outlineColor, lBase, lTop, strokeWidth = 2.2f)

        // Soft radial glow aura
        drawCircle(lanternGlow.copy(alpha = 0.25f * lanternFlicker), radius = 12f, center = lTop)
        drawCircle(lanternGlow, radius = 5.2f, center = lTop)
    }
}

// 7. The City & Clocktower Spire (Day 271+ / Year 1) with Rippling Fountain
private fun DrawScope.drawTheCityAndClocktower(
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    dayNumber: Int,
    saturation: Float,
    isDark: Boolean,
    waterGleam: Float
) {
    val outlineColor = if (isDark) Color(0xFF485160) else Color(0xFF2B2823)

    // Central Civic Plaza Fountain with water ripples
    val fPos = isoToScreen(-1.5f, -4.5f, 0f, centerX, centerY, tileSize)
    val fTop = isoToScreen(-1.5f, -4.5f, 0.85f, centerX, centerY, tileSize)

    val stonePlazaColor = satColor(Color(0xFF333A46), Color(0xFFCED4DA), saturation)
    val fountainWaterColor = satColor(Color(0xFF23303D), Color(0xFF48CAE4), saturation)

    drawCircle(stonePlazaColor, radius = 23f, center = fPos)
    drawCircle(fountainWaterColor, radius = 18f, center = fPos)
    // Water ripple ring
    drawCircle(Color.White.copy(alpha = 0.35f * waterGleam), radius = 12f + waterGleam * 4f, center = fPos, style = Stroke(width = 1f))
    drawCircle(outlineColor, radius = 23f, center = fPos, style = Stroke(width = 1.2f))

    drawLine(stonePlazaColor, fPos, fTop, strokeWidth = 4.5f, cap = StrokeCap.Round)
    drawCircle(fountainWaterColor, radius = 6.5f, center = fTop)

    // Grand Isometric Clocktower Spire
    val twX = -4.8f
    val twY = -3.2f
    val twBase = isoToScreen(twX, twY, 0f, centerX, centerY, tileSize)
    val twBelfry = isoToScreen(twX, twY, 4.3f, centerX, centerY, tileSize)
    val twPeak = isoToScreen(twX, twY, 6.3f, centerX, centerY, tileSize)

    val towerStone = satColor(Color(0xFF262C36), Color(0xFFE9ECEF), saturation)
    val spireColor = satColor(Color(0xFF1B2028), Color(0xFF2B9348), saturation)
    val clockGold = satColor(Color(0xFF8A8478), Color(0xFFFFD166), saturation)

    drawRect(towerStone, topLeft = Offset(twBase.x - 17f, twBelfry.y), size = Size(34f, twBase.y - twBelfry.y))
    drawRect(outlineColor, topLeft = Offset(twBase.x - 17f, twBelfry.y), size = Size(34f, twBase.y - twBelfry.y), style = Stroke(width = 1.5f))

    // Clock Face with glowing hands
    val clockCenter = Offset(twBase.x, twBelfry.y + 18f)
    drawCircle(clockGold, radius = 8.5f, center = clockCenter)
    drawCircle(outlineColor, radius = 8.5f, center = clockCenter, style = Stroke(width = 1.2f))
    drawLine(outlineColor, clockCenter, Offset(clockCenter.x, clockCenter.y - 5.5f), strokeWidth = 1.2f)
    drawLine(outlineColor, clockCenter, Offset(clockCenter.x + 4.5f, clockCenter.y), strokeWidth = 1.2f)

    val spirePath = Path().apply {
        moveTo(twBelfry.x - 19f, twBelfry.y)
        lineTo(twPeak.x, twPeak.y)
        lineTo(twBelfry.x + 19f, twBelfry.y)
        close()
    }
    drawPath(spirePath, color = spireColor)
    drawPath(spirePath, color = outlineColor, style = Stroke(width = 1.4f))

    drawCircle(clockGold, radius = 4f, center = twPeak)
}

// 8. Subtle Floating Breeze Petals (Realism & Motion)
private fun DrawScope.drawAmbientDriftingPetals(
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    ambientPhase: Float,
    saturation: Float
) {
    if (saturation < 0.2f) return

    val petalColor = satColor(Color(0x55AAAAAA), Color(0xCCF4978E), saturation)
    val count = 5
    for (i in 0 until count) {
        val p = (ambientPhase + i * 1.3f) % 6.28318f
        val fraction = p / 6.28318f

        val startIsoX = -3f + (i * 2f)
        val startIsoY = -4f + (i * 1.5f)

        val currIsoX = startIsoX + fraction * 6f
        val currIsoY = startIsoY + fraction * 5f
        val currIsoZ = 1.5f - fraction * 1.0f + sin(p * 2f) * 0.3f

        val pos = isoToScreen(currIsoX, currIsoY, currIsoZ, centerX, centerY, tileSize)
        drawCircle(petalColor, radius = 2.6f, center = pos)
    }
}

// 9. Bloom Ceremony Animation
private fun DrawScope.drawBloomCeremony(
    centerX: Float,
    centerY: Float,
    tileSize: Float,
    progress: Float
) {
    val origin = isoToScreen(0f, 0f, 0.5f, centerX, centerY, tileSize)
    val maxRadius = 190f
    val waveRadius = progress * maxRadius
    val waveAlpha = (1f - progress).coerceIn(0f, 1f)

    drawCircle(
        color = Color(0xFFFFD166).copy(alpha = waveAlpha * 0.45f),
        radius = waveRadius,
        center = origin
    )

    drawCircle(
        color = Color(0xFF52B788).copy(alpha = waveAlpha * 0.7f),
        radius = waveRadius,
        center = origin,
        style = Stroke(width = 2.5f)
    )

    val petalCount = 12
    for (i in 0 until petalCount) {
        val angle = (i * (360f / petalCount)) * (3.14159f / 180f)
        val dist = progress * (115f + (i % 3) * 20f)
        val px = origin.x + cos(angle) * dist
        val py = origin.y + sin(angle) * dist * 0.65f - (progress * 25f)

        val petalColor = if (i % 2 == 0) Color(0xFF52B788) else Color(0xFFF4978E)
        drawCircle(
            color = petalColor.copy(alpha = waveAlpha),
            radius = (1f - progress * 0.4f) * 6.5f,
            center = Offset(px, py)
        )
    }
}

// 10. Blueprint Framing
private fun DrawScope.drawSketchbookAnnotations(
    dayNumber: Int,
    saturation: Float,
    width: Float,
    height: Float,
    isDark: Boolean
) {
    val inkColor = if (isDark) Color(0xFF5E6778) else Color(0xFF7D776C)
    val cornerLen = 22f
    val pad = 16f

    drawLine(inkColor, Offset(pad, pad), Offset(pad + cornerLen, pad), strokeWidth = 1.2f)
    drawLine(inkColor, Offset(pad, pad), Offset(pad, pad + cornerLen), strokeWidth = 1.2f)

    drawLine(inkColor, Offset(width - pad, pad), Offset(width - pad - cornerLen, pad), strokeWidth = 1.2f)
    drawLine(inkColor, Offset(width - pad, pad), Offset(width - pad, pad + cornerLen), strokeWidth = 1.2f)

    drawLine(inkColor, Offset(pad, height - pad), Offset(pad + cornerLen, height - pad), strokeWidth = 1.2f)
    drawLine(inkColor, Offset(pad, height - pad), Offset(pad, height - pad - cornerLen), strokeWidth = 1.2f)

    drawLine(inkColor, Offset(width - pad, height - pad), Offset(width - pad - cornerLen, height - pad), strokeWidth = 1.2f)
    drawLine(inkColor, Offset(width - pad, height - pad), Offset(width - pad, height - pad - cornerLen), strokeWidth = 1.2f)
}
