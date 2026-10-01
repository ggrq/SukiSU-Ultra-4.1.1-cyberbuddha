package com.sukisu.ultra.ui.cyberfx

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.sukisu.ultra.ui.theme.CyberBuddhaPalette
import kotlin.math.sin

/**
 * 赛博朋克佛教 · 电子佛龛全屏背景特效
 *
 * 由五层构成：
 * 1. 虚空黑基底
 * 2. 佛光金呼吸光晕（径向渐变，动画）
 * 3. 霓虹曼陀罗网格（同心圆 + 放射线）
 * 4. 扫描金线（上下巡游）
 * 5. 金尘粒子（漂浮）
 *
 * 纯 Canvas 绘制，无外部资源，性能开销小。
 */
@Composable
fun CyberBuddhaBackground(
    modifier: Modifier = Modifier,
    intensity: Float = 1f
) {
    val transition = rememberInfiniteTransition(label = "cyberbuddha")
    val breath by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )
    val scanY by transition.animateFloat(
        initialValue = 0.02f,
        targetValue = 0.98f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan"
    )
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val widthPx = size.width
        val heightPx = size.height

        // 1. 虚空基底
        drawRect(color = CyberBuddhaPalette.Void)

        // 2. 佛光呼吸光晕（顶部中央，金色径向渐变）
        val haloCenter = Offset(widthPx * 0.5f, heightPx * 0.12f)
        val haloRadius = widthPx * 0.95f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.34f * breath * intensity),
                    CyberBuddhaPalette.NeonViolet.copy(alpha = 0.16f * intensity),
                    Color.Transparent
                ),
                center = haloCenter,
                radius = haloRadius
            ),
            radius = haloRadius,
            center = haloCenter
        )

        // 紫色侧光（左上，静态弱光）
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CyberBuddhaPalette.NeonPurple.copy(alpha = 0.12f * intensity),
                    Color.Transparent
                ),
                center = Offset(widthPx * 0.12f, heightPx * 0.3f),
                radius = widthPx * 0.8f
            ),
            radius = widthPx * 0.8f,
            center = Offset(widthPx * 0.12f, heightPx * 0.3f)
        )

        // 3. 曼陀罗网格（顶部中央，随 drift 缓慢旋转）
        val mandalaCenter = Offset(widthPx * 0.5f, heightPx * 0.14f)
        val maxR = widthPx * 0.42f
        val ringRatios = listOf(0.18f, 0.36f, 0.55f, 0.74f, 0.92f)
        val ringAlpha = 0.10f * intensity
        rotate(degrees = drift * 0.05f, pivot = mandalaCenter) {
            ringRatios.forEach { ratio ->
                drawCircle(
                    color = CyberBuddhaPalette.BuddhaGold.copy(alpha = ringAlpha),
                    radius = maxR * ratio,
                    center = mandalaCenter,
                    style = Stroke(width = 1.2f)
                )
            }
            // 放射线 16 条
            val spokes = 16
            for (i in 0 until spokes) {
                val angle = (i * 360f / spokes) * (Math.PI / 180.0)
                val cosA = kotlin.math.cos(angle).toFloat()
                val sinA = kotlin.math.sin(angle).toFloat()
                drawLine(
                    color = CyberBuddhaPalette.NeonViolet.copy(alpha = 0.07f * intensity),
                    start = Offset(
                        mandalaCenter.x + cosA * maxR * 0.18f,
                        mandalaCenter.y + sinA * maxR * 0.18f
                    ),
                    end = Offset(
                        mandalaCenter.x + cosA * maxR,
                        mandalaCenter.y + sinA * maxR
                    ),
                    strokeWidth = 1f
                )
            }
        }

        // 底部：淡青网格地平线
        val gridStep = widthPx * 0.1f
        var gx: Float = 0f
        while (gx <= widthPx) {
            drawLine(
                color = CyberBuddhaPalette.NeonCyan.copy(alpha = 0.035f * intensity),
                start = Offset(gx, heightPx * 0.62f),
                end = Offset(gx, heightPx),
                strokeWidth = 0.8f
            )
            gx += gridStep
        }
        var gy: Float = heightPx * 0.62f
        while (gy <= heightPx) {
            drawLine(
                color = CyberBuddhaPalette.NeonCyan.copy(alpha = 0.035f * intensity),
                start = Offset(0f, gy),
                end = Offset(widthPx, gy),
                strokeWidth = 0.8f
            )
            gy += gridStep * 0.8f
        }

        // 4. 扫描金线（水平巡游，带拖尾渐隐）
        val scanPos = heightPx * scanY
        val scanHalf = heightPx * 0.03f
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.05f * intensity),
                    CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.22f * intensity),
                    CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.05f * intensity),
                    Color.Transparent
                ),
                startY = scanPos - scanHalf,
                endY = scanPos + scanHalf
            ),
            topLeft = Offset(0f, scanPos - scanHalf),
            size = Size(widthPx, scanHalf * 2f)
        )
        drawLine(
            color = CyberBuddhaPalette.GoldBright.copy(alpha = 0.5f * intensity),
            start = Offset(0f, scanPos),
            end = Offset(widthPx, scanPos),
            strokeWidth = 1.4f
        )

        // 5. 金尘粒子（8 颗，缓慢漂浮）
        val particleCount = 8
        val seed = 137
        for (i in 0 until particleCount) {
            val fx = ((seed * (i + 3)) % 97) / 97f
            val fyBase = ((seed * (i + 7)) % 89) / 89f
            val phase = i * 1.7f
            val bob = (sin(phase + drift * 0.02f) + 1f) / 2f
            val py = (fyBase * 0.8f + 0.12f + bob * 0.08f) * heightPx
            val px = (fx * 0.9f + 0.05f) * widthPx
            val alpha = (0.12f + 0.2f * bob) * intensity
            drawCircle(
                color = if (i % 3 == 0) {
                    CyberBuddhaPalette.NeonCyan.copy(alpha = alpha)
                } else {
                    CyberBuddhaPalette.BuddhaGold.copy(alpha = alpha)
                },
                radius = (1.2f + 1.6f * bob) * (1f + (i % 3) * 0.5f),
                center = Offset(px, py)
            )
        }
    }
}
