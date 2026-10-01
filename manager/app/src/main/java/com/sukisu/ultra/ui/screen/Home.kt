package com.sukisu.ultra.ui.screen

import android.content.Context
import android.os.Build
import android.system.Os
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.sukisu.ultra.KernelVersion
import com.sukisu.ultra.Natives
import com.sukisu.ultra.R
import com.sukisu.ultra.getKernelVersion
import com.sukisu.ultra.ui.LocalPagerState
import com.sukisu.ultra.ui.component.CyberBackdrop
import com.sukisu.ultra.ui.component.DropdownItem
import com.sukisu.ultra.ui.component.rememberConfirmDialog
import com.sukisu.ultra.ui.navigation3.Navigator
import com.sukisu.ultra.ui.navigation3.Route
import com.sukisu.ultra.ui.theme.CyberBuddhaPalette
import com.sukisu.ultra.ui.theme.isInDarkTheme
import com.sukisu.ultra.ui.util.*
import com.sukisu.ultra.ui.util.module.LatestVersionInfo
import com.sukisu.ultra.ui.util.reboot
import com.sukisu.ultra.ui.util.rootAvailable
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.isDynamicColor
import top.yukonga.miuix.kmp.utils.PressFeedbackType

/**
 * 赛博朋克佛教 · 电子佛龛主屏
 * 布局完全脱离官方骨架：无顶部标题栏，改为「佛龛神坛 + 功德簿 + 参禅录」三段式。
 */
@Composable
fun HomePager(
    navigator: Navigator,
    bottomInnerPadding: Dp
) {
    val kernelVersion = getKernelVersion()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val checkUpdate = prefs.getBoolean("check_update", true)
    val themeMode = prefs.getInt("color_mode", 5)

    val isManager = Natives.isManager
    val ksuVersion = if (isManager) Natives.version else null
    val lkmMode = ksuVersion?.let {
        if (kernelVersion.isGKI()) Natives.isLkmMode else null
    }
    val pageState = LocalPagerState.current
    val coroutineScope = rememberCoroutineScope()

    CyberBackdrop(useShrine = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp)
        ) {
            Spacer(Modifier.height(12.dp))

        // ── 佛龛神坛（替代原状态卡组合） ──
        BuddhaAltar(
            kernelVersion = kernelVersion,
            ksuVersion = ksuVersion,
            lkmMode = lkmMode,
            themeMode = themeMode,
            onClickInstall = { navigator.push(Route.Install) }
        )

        // ── 功德簿（替代右侧双卡） ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MeritCard(
                modifier = Modifier.weight(1f),
                title = "度众生",
                subTitle = "超级用户",
                count = getSuperuserCount(),
                icon = { Icon(Icons.Rounded.Security, null, tint = CyberBuddhaPalette.GoldBright, modifier = Modifier.size(20.dp)) },
                accent = CyberBuddhaPalette.GoldBright,
                onClick = {
                    coroutineScope.launch {
                        pageState.animateScrollToPage(page = 1, animationSpec = tween(easing = EaseInOut))
                    }
                }
            )
            MeritCard(
                modifier = Modifier.weight(1f),
                title = "传法器",
                subTitle = "模块",
                count = getModuleCount(),
                icon = { Icon(Icons.Rounded.Extension, null, tint = CyberBuddhaPalette.NeonCyan, modifier = Modifier.size(20.dp)) },
                accent = CyberBuddhaPalette.NeonCyan,
                onClick = {
                    coroutineScope.launch {
                        pageState.animateScrollToPage(page = 2, animationSpec = tween(easing = EaseInOut))
                    }
                }
            )
        }

        // 警告（内核版本/授权失败）
        if (isManager && Natives.requireNewKernel()) {
            WarningCard(
                stringResource(id = R.string.require_kernel_version)
                    .format(ksuVersion, Natives.MINIMAL_SUPPORTED_KERNEL),
                themeMode
            )
        }
        if (ksuVersion != null && !rootAvailable()) {
            WarningCard(
                stringResource(id = R.string.grant_root_failed),
                themeMode
            )
        }

        // ── 参禅录（替代原信息卡） ──
        ZenRecordCard()

        if (checkUpdate) {
            UpdateCard(themeMode)
        }
        Spacer(Modifier.height(bottomInnerPadding + 16.dp))
        }
    }
}

/**
 * 佛龛神坛：整块发光主卡。
 * 无顶栏，标题/版本/状态/重启全部并入神龛。
 */
@Composable
private fun BuddhaAltar(
    kernelVersion: KernelVersion,
    ksuVersion: Int?,
    lkmMode: Boolean?,
    themeMode: Int,
    onClickInstall: () -> Unit
) {
    val breath = rememberInfiniteTransition(label = "altarBreath")
    val haloAlpha by breath.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.4f),
                shape = RoundedCornerShape(28.dp)
            ),
        colors = CardDefaults.defaultColors(
            color = Color(0xE60D1022)
        ),
        insideMargin = PaddingValues(0.dp),
        onClick = {
            if (kernelVersion.isGKI()) onClickInstall()
        },
        showIndication = true,
        pressFeedbackType = PressFeedbackType.Tilt
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x330D1022),
                            Color(0xAA0D1022),
                            Color(0xF00D1022)
                        )
                    )
                )
                .padding(vertical = 26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 状态徽章
                when {
                    ksuVersion != null -> {
                        val safeMode = if (Natives.isSafeMode) " [${stringResource(id = R.string.safe_mode)}]" else ""
                        val workingMode = when (lkmMode) {
                            null -> ""
                            true -> " <LKM>"
                            else -> " <Built-in>"
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.12f))
                                .border(1.dp, CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                Icons.Rounded.CheckCircleOutline,
                                null,
                                tint = CyberBuddhaPalette.BuddhaGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${stringResource(id = R.string.home_working)}$workingMode$safeMode",
                                color = CyberBuddhaPalette.GoldBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    kernelVersion.isGKI() -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberBuddhaPalette.Cinnabar.copy(alpha = 0.12f))
                                .border(1.dp, CyberBuddhaPalette.Cinnabar.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Icon(Icons.Rounded.ErrorOutline, null, tint = CyberBuddhaPalette.Cinnabar, modifier = Modifier.size(14.dp))
                            Text(
                                text = stringResource(R.string.home_not_installed),
                                color = CyberBuddhaPalette.Cinnabar,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    else -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberBuddhaPalette.TextMuted.copy(alpha = 0.15f))
                                .border(1.dp, CyberBuddhaPalette.TextMuted.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Icon(Icons.Rounded.ErrorOutline, null, tint = CyberBuddhaPalette.TextMuted, modifier = Modifier.size(14.dp))
                            Text(
                                text = stringResource(R.string.home_unsupported),
                                color = CyberBuddhaPalette.TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // 中央发光莲花（呼吸）
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.30f * haloAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Icon(
                        Icons.Rounded.Spa,
                        null,
                        tint = CyberBuddhaPalette.GoldBright.copy(alpha = 0.6f + 0.4f * haloAlpha),
                        modifier = Modifier.size(64.dp)
                    )
                }

                // 双环法印：左重启 / 右装藏
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        RebootRingButton()
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.reboot),
                            fontSize = 10.sp,
                            color = CyberBuddhaPalette.TextMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        RingButton(
                            icon = Icons.Rounded.Spa,
                            tint = CyberBuddhaPalette.NeonViolet,
                            borderColor = CyberBuddhaPalette.NeonViolet.copy(alpha = 0.5f),
                            onClick = onClickInstall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "装藏",
                            fontSize = 10.sp,
                            color = CyberBuddhaPalette.TextMuted
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "电子佛龛",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp,
                    color = CyberBuddhaPalette.GoldBright,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "SukiSU Ultra · 内核权限管理器",
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = CyberBuddhaPalette.NeonViolet.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                if (ksuVersion != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "版本 v$ksuVersion",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = CyberBuddhaPalette.TextSecondary
                    )
                }
            }
        }
    }
}

/** 功德簿卡：数字 + 佛语标签 */
@Composable
private fun MeritCard(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    count: Int,
    accent: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                color = accent.copy(alpha = 0.3f),
                shape = RoundedCornerShape(22.dp)
            ),
        colors = CardDefaults.defaultColors(
            color = Color(0xB30D1022)
        ),
        insideMargin = PaddingValues(0.dp),
        onClick = onClick,
        showIndication = true,
        pressFeedbackType = PressFeedbackType.Tilt
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon()
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accent
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = count.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = CyberBuddhaPalette.GoldBright
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subTitle,
                fontSize = 11.sp,
                color = CyberBuddhaPalette.TextMuted
            )
        }
    }
}

/** 参禅录：系统信息禅意两栏 */
@Composable
private fun ZenRecordCard() {
    val manualHookText = stringResource(R.string.manual_hook)
    val inlineHookText = stringResource(R.string.inline_hook)
    val tracepointHookText = stringResource(R.string.tracepoint_hook)
    val unknownHookText = stringResource(R.string.selinux_status_unknown)
    val susfsInfo = rememberSusfsInfo(manualHookText, inlineHookText)
    val isSusfsSupported = susfsInfo.status == SusfsStatus.Supported
    val hookTypeLabel = remember(manualHookText, inlineHookText, tracepointHookText) {
        val localized = when (val rawType = Natives.getHookType()) {
            "Manual" -> manualHookText
            "Tracepoint" -> tracepointHookText
            else -> rawType
        }
        localized.ifBlank { unknownHookText }
    }
    val context = LocalContext.current
    val uname = Os.uname()
    val managerVersion = getManagerVersion(context)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .border(1.dp, CyberBuddhaPalette.NeonViolet.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.defaultColors(
            color = Color(0xB30D1022)
        ),
        insideMargin = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.Spa, null, tint = CyberBuddhaPalette.NeonViolet, modifier = Modifier.size(16.dp))
                Text(
                    text = "参禅 · 系统实相",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = CyberBuddhaPalette.NeonViolet.copy(alpha = 0.95f)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(CyberBuddhaPalette.NeonViolet.copy(alpha = 0.2f))
                )
            }

            Spacer(Modifier.height(14.dp))

            ZenRow("内核", uname.release)
            ZenRow("管理器", "${managerVersion.first} (${managerVersion.second})")
            if (isSusfsSupported) {
                ZenRow("SusFS", susfsInfo.detail)
            } else {
                ZenRow("钩子", hookTypeLabel)
            }
            ZenRow("SELinux", getSELinuxStatus())
            Spacer(Modifier.height(6.dp))
            Text(
                text = Build.FINGERPRINT,
                fontSize = 10.sp,
                color = CyberBuddhaPalette.TextMuted.copy(alpha = 0.7f),
                lineHeight = 14.sp
            )
        }
    }
}

/** 参禅行：金刚标签 + 值 */
@Composable
private fun ZenRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.1f))
                .border(1.dp, CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = CyberBuddhaPalette.BuddhaGold,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(Modifier.size(12.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = CyberBuddhaPalette.TextSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun UpdateCard(
    themeMode: Int,
) {
    val context = LocalContext.current
    val latestVersionInfo = LatestVersionInfo()
    val newVersion by produceState(initialValue = latestVersionInfo) {
        value = withContext(Dispatchers.IO) {
            checkNewVersion()
        }
    }

    val currentVersionCode = getManagerVersion(context).second
    val newVersionCode = newVersion.versionCode
    val newVersionUrl = newVersion.downloadUrl
    val changelog = newVersion.changelog

    val uriHandler = LocalUriHandler.current
    val title = stringResource(id = R.string.module_changelog)
    val updateText = stringResource(id = R.string.module_update)

    AnimatedVisibility(
        visible = newVersionCode > currentVersionCode,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val updateDialog = rememberConfirmDialog(onConfirm = { uriHandler.openUri(newVersionUrl) })
        WarningCard(
            message = stringResource(id = R.string.new_version_available).format(newVersionCode),
            themeMode = themeMode,
            color = colorScheme.outline
        ) {
            if (changelog.isEmpty()) {
                uriHandler.openUri(newVersionUrl)
            } else {
                updateDialog.showConfirm(
                    title = title,
                    content = changelog,
                    markdown = true,
                    confirm = updateText
                )
            }
        }
    }
}

@Composable
fun RebootDropdownItem(
    @StringRes id: Int, reason: String = "",
    showTopPopup: MutableState<Boolean>,
    optionSize: Int,
    index: Int,
) {
    DropdownItem(
        text = stringResource(id),
        optionSize = optionSize,
        onSelectedIndexChange = {
            reboot(reason)
            showTopPopup.value = false
        },
        index = index
    )
}

@Composable
fun WarningCard(
    message: String,
    themeMode: Int,
    color: Color? = null,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier.padding(top = 14.dp),
        onClick = {
            onClick?.invoke()
        },
        colors = CardDefaults.defaultColors(
            color = color ?: when {
                isDynamicColor -> colorScheme.errorContainer
                isInDarkTheme(themeMode) -> Color(0xCC2A1030)
                else -> Color(0xFFF8E2E2)
            }
        ),
        showIndication = onClick != null,
        pressFeedbackType = PressFeedbackType.Tilt
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = message,
                color = if (isDynamicColor) colorScheme.onErrorContainer else Color(0xFFF72727),
                fontSize = 14.sp
            )
        }
    }
}

fun getManagerVersion(context: Context): Pair<String, Long> {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)!!
    val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
    return Pair(packageInfo.versionName!!, versionCode)
}

private enum class SusfsStatus {
    Idle, Loading, Supported, Unsupported, Error
}

private data class SusfsInfoState(
    val status: SusfsStatus = SusfsStatus.Idle,
    val detail: String = "",
)

@Composable
private fun rememberSusfsInfo(
    manualHookLabel: String,
    inlineHookLabel: String,
): SusfsInfoState {
    return remember(manualHookLabel, inlineHookLabel) {
        runCatching {
            val supported = getSuSFSStatus().equals("true", ignoreCase = true)
            if (supported) {
                val version = getSuSFSVersion().trim()
                val hookLabel = when (val type = Natives.getHookType()) {
                    "Manual" -> manualHookLabel
                    "Inline" -> inlineHookLabel
                    else -> type
                }.takeIf { it.isNotBlank() }?.let { "($it)" }.orEmpty()
                SusfsInfoState(
                    status = SusfsStatus.Supported,
                    detail = listOf(version, hookLabel)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                )
            } else {
                SusfsInfoState(
                    status = SusfsStatus.Unsupported,
                    detail = ""
                )
            }
        }.getOrElse {
            SusfsInfoState(status = SusfsStatus.Error)
        }
    }
}

/** 金环法印按钮：圆形金描边 + 图标 */
@Composable
private fun RingButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(50))
            .background(tint.copy(alpha = 0.1f))
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

/** 重启法印按钮：莲花左侧金环，点击弹出重启菜单 */
@Composable
private fun RebootRingButton() {
    val showTopPopup = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(50))
            .background(CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.12f))
            .border(1.dp, CyberBuddhaPalette.BuddhaGold.copy(alpha = 0.5f), RoundedCornerShape(50))
            .clickable { showTopPopup.value = true },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.PowerSettingsNew,
            contentDescription = stringResource(R.string.reboot),
            tint = CyberBuddhaPalette.GoldBright,
            modifier = Modifier.size(22.dp)
        )
    }
    top.yukonga.miuix.kmp.extra.SuperListPopup(
        show = showTopPopup,
        popupPositionProvider = top.yukonga.miuix.kmp.basic.ListPopupDefaults.ContextMenuPositionProvider,
        alignment = top.yukonga.miuix.kmp.basic.PopupPositionProvider.Align.TopEnd,
        onDismissRequest = {
            showTopPopup.value = false
        }
    ) {
        val pm = LocalContext.current.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager?
        @Suppress("DEPRECATION")
        val isRebootingUserspaceSupported =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R
                    && pm?.isRebootingUserspaceSupported == true

        top.yukonga.miuix.kmp.basic.ListPopupColumn {
            val rebootOptions = mutableListOf(
                Pair(R.string.reboot, ""),
                Pair(R.string.reboot_recovery, "recovery"),
                Pair(R.string.reboot_bootloader, "bootloader"),
                Pair(R.string.reboot_download, "download"),
                Pair(R.string.reboot_edl, "edl")
            )
            if (isRebootingUserspaceSupported) {
                rebootOptions.add(1, Pair(R.string.reboot_userspace, "userspace"))
            }
            rebootOptions.forEachIndexed { idx, (id, reason) ->
                RebootDropdownItem(
                    id = id,
                    reason = reason,
                    showTopPopup = showTopPopup,
                    optionSize = rebootOptions.size,
                    index = idx
                )
            }
        }
    }
}
