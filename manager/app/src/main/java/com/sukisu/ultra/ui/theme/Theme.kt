package com.sukisu.ultra.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.sukisu.ultra.ui.webui.MonetColorsProvider.UpdateCss
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

/**
 * 赛博朋克佛教主题色板（全局可用）
 * 以「虚空黑 + 赛博霓虹紫 + 佛光金」为核心视觉语言。
 * 电子佛龛 · Cyber Buddha
 */
object CyberBuddhaPalette {
    // ===== 基底（虚空五色） =====
    val Void = Color(0xFF05060D)          // 虚空黑（主背景）
    val VoidLight = Color(0xFF0D1022)     // 虚空亮（卡片容器）
    val VoidDeep = Color(0xFF030409)      // 深渊黑（深层容器）
    val InkBlue = Color(0xFF10172F)       // 墨蓝（次级容器/边框）
    val InkGlow = Color(0xFF1A2344)       // 墨蓝辉（悬浮/高亮容器）

    // ===== 赛博霓虹（六道霓虹） =====
    val NeonViolet = Color(0xFFA855F7)    // 霓虹紫（主强调）
    val NeonPurple = Color(0xFF7C3AED)    // 深紫（渐变）
    val NeonCyan = Color(0xFF22D3EE)      // 霓虹青（信息/数据）
    val NeonPink = Color(0xFFF472B6)      // 霓虹粉（点缀/莲）
    val NeonBlue = Color(0xFF3B82F6)      // 霓虹蓝（辅助）
    val NeonGreen = Color(0xFF34D399)     // 霓虹绿（成功/在线）

    // ===== 佛光金系（金身三色） =====
    val BuddhaGold = Color(0xFFFFC94A)    // 佛光金（主强调）
    val GoldBright = Color(0xFFFFE08A)    // 亮金（文字高亮）
    val GoldDark = Color(0xFFC9962E)      // 暗金（渐变/边框）
    val GoldGlow = Color(0x26FFC94A)      // 金辉（光晕/淡底）

    // ===== 朱砂 & 莲（肉身六色） =====
    val Cinnabar = Color(0xFFE63946)      // 朱砂红（警告/危险）
    val LotusPink = Color(0xFFE8A0B4)     // 莲粉（柔和点缀）
    val Amber = Color(0xFFF59E0B)         // 琥珀（进行中）

    // ===== 文本（贝叶三色） =====
    val TextPrimary = Color(0xFFEEEEF6)   // 主文字
    val TextSecondary = Color(0xFFA9B1CC) // 次级文字
    val TextMuted = Color(0xFF6F77A0)     // 弱化文字

    // ===== 渐变 =====
    /** 佛光金渐变 */
    fun goldGradient(alpha: Float = 1f) = listOf(
        Color(0xFFFFC94A).copy(alpha = alpha),
        Color(0xFFC9962E).copy(alpha = alpha)
    )

    /** 霓虹紫渐变 */
    fun neonGradient(alpha: Float = 1f) = listOf(
        Color(0xFFA855F7).copy(alpha = alpha),
        Color(0xFF7C3AED).copy(alpha = alpha)
    )

    /** 佛光放射渐变（背景用：中心金 → 外圈紫 → 虚空黑） */
    fun haloGradient(alpha: Float = 1f) = listOf(
        Color(0xFFFFC94A).copy(alpha = alpha * 0.85f),
        Color(0xFFA855F7).copy(alpha = alpha * 0.55f),
        Color(0xFF05060D).copy(alpha = alpha)
    )

    /** 虚空渐变（卡片用） */
    fun voidGradient(alpha: Float = 1f) = listOf(
        Color(0xFF0D1022).copy(alpha = alpha),
        Color(0xFF05060D).copy(alpha = alpha)
    )
}

@Composable
fun KernelSUTheme(
    colorMode: Int = 0,
    keyColor: Color? = null,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    // 赛博朋克佛教主题：默认佛光金为主色
    val effectiveKeyColor = keyColor ?: CyberBuddhaPalette.BuddhaGold
    val controller = when (colorMode) {
        1 -> ThemeController(ColorSchemeMode.Light)
        2 -> ThemeController(ColorSchemeMode.Dark)
        3 -> ThemeController(
            ColorSchemeMode.MonetSystem,
            keyColor = effectiveKeyColor,
            isDark = isDark
        )

        4 -> ThemeController(
            ColorSchemeMode.MonetLight,
            keyColor = effectiveKeyColor,
        )

        5 -> ThemeController(
            ColorSchemeMode.MonetDark,
            keyColor = effectiveKeyColor,
        )

        else -> ThemeController(ColorSchemeMode.System)
    }
    return MiuixTheme(
        controller = controller,
        content = {
            UpdateCss()
            content()
        }
    )
}

@Composable
@ReadOnlyComposable
fun isInDarkTheme(themeMode: Int): Boolean {
    return when (themeMode) {
        1, 4 -> false  // Force light mode
        2, 5 -> true   // Force dark mode
        else -> isSystemInDarkTheme()  // Follow system (0 or default)
    }
}
