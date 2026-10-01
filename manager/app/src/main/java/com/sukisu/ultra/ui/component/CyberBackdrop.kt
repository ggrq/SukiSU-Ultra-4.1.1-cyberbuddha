package com.sukisu.ultra.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sukisu.ultra.R

/**
 * 赛博佛堂整页背景：
 * - 底部垫不透明的虚空黑，保证任何主题下页面都是深色赛博底
 * - 叠加生成的全息佛像/经文背景图（固定不滚动）
 * - 上下渐变遮罩，保证前景内容可读
 */
@Composable
fun CyberBackdrop(
    useShrine: Boolean = false,
    imageAlpha: Float = 0.26f,
    content: @Composable () -> Unit
) {
    val bgRes = if (useShrine) R.drawable.cyber_shrine_bg else R.drawable.cyber_sutra_bg
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0714))
    ) {
        Image(
            painter = painterResource(bgRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(imageAlpha),
            contentScale = ContentScale.Crop
        )
        // 上下暗化遮罩：顶部稍亮保顶栏可读，底部压暗保列表可读
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x400A0714),
                            Color(0x1A0A0714),
                            Color(0x990A0714),
                            Color(0xE60A0714)
                        )
                    )
                )
        )
        content()
    }
}
