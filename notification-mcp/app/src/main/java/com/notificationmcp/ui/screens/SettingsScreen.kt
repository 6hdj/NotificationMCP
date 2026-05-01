package com.notificationmcp.ui.screens

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notificationmcp.ui.components.GlassCard
import com.notificationmcp.ui.components.GlassDivider
import com.notificationmcp.ui.theme.*
import com.notificationmcp.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }
    var showDataDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // ── Header ──
        Text(
            text = "设置",
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        // ── 服务控制 ──
        SectionHeader("服务", isDark)
        GlassCard(
            isDark = isDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            SettingsSwitchItem(
                title = "启用通知监听",
                subtitle = if (state.serviceEnabled) "服务运行中" else "服务已停止",
                checked = state.serviceEnabled,
                onCheckedChange = { viewModel.toggleService() },
                isDark = isDark
            )
        }

        // ── 通知处理 ──
        SectionHeader("通知处理", isDark)
        GlassCard(
            isDark = isDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Column {
                SettingsSwitchItem(
                    title = "通知过滤",
                    subtitle = "过滤低优先级通知",
                    checked = state.filterEnabled,
                    onCheckedChange = { viewModel.toggleFilter() },
                    isDark = isDark
                )
                GlassDivider(isDark = isDark)
                SettingsSwitchItem(
                    title = "AI 摘要",
                    subtitle = "使用 AI 生成通知摘要",
                    checked = state.aiSummaryEnabled,
                    onCheckedChange = { viewModel.toggleAISummary() },
                    isDark = isDark
                )
                GlassDivider(isDark = isDark)
                SettingsSwitchItem(
                    title = "自动复制验证码",
                    subtitle = "检测到验证码时自动复制到剪贴板",
                    checked = state.autoCopyEnabled,
                    onCheckedChange = { viewModel.toggleAutoCopy() },
                    isDark = isDark
                )
            }
        }

                // ── 隐私 ──
        SectionHeader("隐私", isDark)
        GlassCard(
            isDark = isDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Column {
                SettingsSwitchItem(
                    title = "自动脱敏",
                    subtitle = "自动隐藏通知中的敏感信息",
                    checked = state.autoRedact,
                    onCheckedChange = { viewModel.setAutoRedact(it) },
                    isDark = isDark
                )
                                GlassDivider(isDark = isDark)
                // 脱敏级别选择器
                var showRedactMenu by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRedactMenu = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("脱敏级别", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            when (state.redactLevel) {
                                com.notificationmcp.data.model.RedactLevel.NONE -> "不脱敏"
                                com.notificationmcp.data.model.RedactLevel.NORMAL -> "标准：隐藏手机号和身份证"
                                com.notificationmcp.data.model.RedactLevel.STRICT -> "严格：隐藏所有敏感信息"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                    DropdownMenu(
                        expanded = showRedactMenu,
                        onDismissRequest = { showRedactMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("不脱敏") },
                            onClick = {
                                viewModel.setRedactLevel(com.notificationmcp.data.model.RedactLevel.NONE)
                                showRedactMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("标准：隐藏手机号和身份证") },
                            onClick = {
                                viewModel.setRedactLevel(com.notificationmcp.data.model.RedactLevel.NORMAL)
                                showRedactMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("严格：隐藏所有敏感信息") },
                            onClick = {
                                viewModel.setRedactLevel(com.notificationmcp.data.model.RedactLevel.STRICT)
                                showRedactMenu = false
                            }
                        )
                    }
                }
            }
        }

        // ── 数据管理 ──
        SectionHeader("数据", isDark)
        GlassCard(
            isDark = isDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Column {
                SettingsNavItem(
                    icon = Icons.Filled.Delete,
                    title = "清除通知数据",
                    subtitle = "删除所有已存储的通知记录",
                    isDark = isDark,
                    onClick = { showClearDialog = true }
                )
                GlassDivider(isDark = isDark)
                SettingsNavItem(
                    icon = Icons.Filled.Storage,
                    title = "数据导出",
                    subtitle = "导出通知数据为 JSON/CSV",
                    isDark = isDark,
                    onClick = { showDataDialog = true }
                )
            }
        }

        // ── 系统 ──
        SectionHeader("系统", isDark)
        GlassCard(
            isDark = isDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Column {
                SettingsNavItem(
                    icon = Icons.Filled.Notifications,
                    title = "通知权限",
                    subtitle = "管理系统通知权限",
                    isDark = isDark,
                    onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        context.startActivity(intent)
                    }
                )
                GlassDivider(isDark = isDark)
                SettingsNavItem(
                    icon = Icons.Filled.BatteryFull,
                    title = "电池优化",
                    subtitle = "将应用加入电池优化白名单",
                    isDark = isDark,
                    onClick = {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        intent.data = android.net.Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                    }
                )
                GlassDivider(isDark = isDark)
                SettingsNavItem(
                    icon = Icons.Filled.Info,
                    title = "关于",
                    subtitle = "NotificationMCP v1.0.0",
                    isDark = isDark,
                    onClick = { showAboutDialog = true }
                )
            }
        }

        // 导出消息
        state.exportMessage?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = msg,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // ── 清除数据确认弹窗 ──
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("清除通知数据") },
            text = { Text("确定要删除所有通知记录吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearData()
                    showClearDialog = false
                }) {
                    Text("清除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // ── 数据导出弹窗 ──
    if (showDataDialog) {
        AlertDialog(
            onDismissRequest = { showDataDialog = false },
            title = { Text("数据导出") },
            text = { Text("选择导出格式") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.exportData("json")
                    showDataDialog = false
                }) {
                    Text("JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.exportData("csv")
                    showDataDialog = false
                }) {
                    Text("CSV")
                }
            }
        )
    }

    // ── 关于弹窗 ──
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("关于 NotificationMCP") },
            text = {
                Column {
                    Text("版本: 1.0.0")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("基于 MCP 协议的通知管理服务")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("支持实时通知捕获、隐私分类、自动化处理")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, isDark: Boolean) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF34C759)
            )
        )
    }
}

@Composable
private fun SettingsNavItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
