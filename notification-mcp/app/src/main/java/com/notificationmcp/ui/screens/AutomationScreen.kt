package com.notificationmcp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notificationmcp.data.db.entity.AutomationRuleEntity
import com.notificationmcp.ui.components.*
import com.notificationmcp.ui.theme.*
import com.notificationmcp.ui.viewmodel.AutomationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationScreen(viewModel: AutomationViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val isDark = isSystemInDarkTheme()

    var showCreator by remember { mutableStateOf(false) }
    var editingTemplate by remember { mutableStateOf<AutomationTemplate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // ── Title ──
        Text(
            text = "自动化",
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        // ── Template Market ──
        Text(
            "模板市场",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(automationTemplates) { template ->
                TemplateCard(
                    template = template,
                    isDark = isDark,
                    onClick = {
                        editingTemplate = template
                        showCreator = true
                    }
                )
            }
        }

        // ── Divider ──
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )

        // ── Active Rules ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "我的规则",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "${state.rules.count { it.isEnabled }}/${state.rules.size} 活跃",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (state.rules.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✨", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "还没有自动化规则",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "从上方模板市场选择一个开始",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            // Rule cards with animated visibility
            state.rules.forEachIndexed { index, rule ->
                key(rule.id) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300, delayMillis = index * 50)) +
                                slideInVertically(tween(300, delayMillis = index * 50)) { it / 4 },
                    ) {
                        RuleCardV2(
                            rule = rule,
                            isDark = isDark,
                            onToggle = { viewModel.toggleRule(rule) },
                            onDelete = { viewModel.deleteRule(rule.id) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // ── Creator Sheet ──
    if (showCreator) {
        RuleCreatorSheet(
            template = editingTemplate,
            isDark = isDark,
            onDismiss = {
                showCreator = false
                editingTemplate = null
            },
            onConfirm = { rule ->
                viewModel.addRuleFromEntity(rule)
                showCreator = false
                editingTemplate = null
            }
        )
    }
}

// ── Template Card (horizontal scroll in market) ──
@Composable
private fun TemplateCard(
    template: AutomationTemplate,
    isDark: Boolean,
    onClick: () -> Unit
) {
        GlassCard(isDark = isDark, modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick), cornerRadius = 20.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Emoji + accent bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(template.emoji, fontSize = 24.sp)
                Box(
                    modifier = Modifier
                        .size(4.dp, 24.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(template.accentColor)
                )
            }

            Text(
                template.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Text(
                template.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                lineHeight = 16.sp
            )

            // "Use template" indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "使用 →",
                    fontSize = 11.sp,
                    color = template.accentColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ── Rule Card V2 (modern) ──
@Composable
private fun RuleCardV2(
    rule: AutomationRuleEntity,
    isDark: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
        GlassCard(isDark = isDark, modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (rule.isEnabled) Color(0xFF34C759) else Color(0xFF8E8E93))
                )
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        rule.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    // Show trigger summary
                    val triggerSummary = buildList {
                        rule.triggerTemplateType?.let { add(templateTypeToLabel(it)) }
                        rule.triggerPackage?.let { add(it.substringAfterLast('.')) }
                        if (rule.triggerTimeMode != "always") add(timeModeToLabel(rule.triggerTimeMode))
                    }.joinToString(" · ")

                    val actionSummary = buildList {
                        if (rule.actionAutoCopy) add("📋 复制")
                        if (rule.actionForceSound) add("🔊 强提醒")
                        if (rule.actionPinInSummary) add("⭐ 置顶")
                        if (rule.actionSilentRemove) add("🗑️ 过滤")
                        if (rule.actionType == "webhook") add("🌐 转发")
                    }.joinToString(" ")

                    Text(
                        triggerSummary.ifEmpty { "所有通知" } + " → " + actionSummary.ifEmpty { rule.actionType },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                // Toggle
                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF34C759)
                    )
                )
            }

            // Match count bar
            if (rule.matchCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "已匹配 ${rule.matchCount} 次",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Rule Creator Bottom Sheet ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RuleCreatorSheet(
    template: AutomationTemplate?,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (AutomationRuleEntity) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var ruleName by remember { mutableStateOf(template?.title ?: "") }
    var selectedApps by remember { mutableStateOf(template?.selectedApps) }
    var triggerType by remember { mutableStateOf(template?.triggerTemplateType) }
    var timeMode by remember { mutableStateOf(template?.triggerTimeMode ?: "always") }
    var customTimeRange by remember { mutableStateOf("08:00-22:00") }
    var dndOverride by remember { mutableStateOf(false) }
    var autoCopy by remember { mutableStateOf(template?.actionAutoCopy ?: false) }
    var forceSound by remember { mutableStateOf(template?.actionForceSound ?: false) }
    var pinSummary by remember { mutableStateOf(template?.actionPinInSummary ?: false) }
    var silentRemove by remember { mutableStateOf(template?.actionSilentRemove ?: false) }
    var webhookUrl by remember { mutableStateOf("") }
    var customRegex by remember { mutableStateOf(template?.triggerContentRegex ?: "") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
                GlassCard(isDark = isDark, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f), cornerRadius = 28.dp) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Header ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (template != null) "使用模板" else "新建规则",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, "关闭")
                    }
                }

                // ── Step indicator ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(1 to "来源", 2 to "条件", 3 to "动作").forEach { (s, label) ->
                        val isActive = step == s
                        val isDone = step > s
                        Box(
                            modifier = Modifier
                                .size(if (isActive) 36.dp else 28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isActive -> MaterialTheme.colorScheme.primary
                                        isDone -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    "$s",
                                    fontSize = if (isActive) 14.sp else 12.sp,
                                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }
                        if (s < 3) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(2.dp)
                                    .background(
                                        if (isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                            )
                        }
                        Text(
                            label,
                            fontSize = 11.sp,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Step content ──
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        slideInHorizontally(tween(250)) { it } + fadeIn(tween(250)) togetherWith
                                slideOutHorizontally(tween(250)) { -it } + fadeOut(tween(250))
                    },
                    label = "stepTransition"
                ) { currentStep ->
                    when (currentStep) {
                                                1 -> StepAppSource(
                            ruleName = ruleName,
                            onNameChange = { ruleName = it },
                            selectedApps = selectedApps,
                            onAppsChange = { selectedApps = it },
                            isDark = isDark
                        )
                        2 -> StepTriggerCondition(
                            triggerType = triggerType,
                            onTriggerTypeChange = { triggerType = it },
                            timeMode = timeMode,
                            onTimeModeChange = { timeMode = it },
                            customTimeRange = customTimeRange,
                            onCustomTimeRangeChange = { customTimeRange = it },
                            dndOverride = dndOverride,
                            onDndOverrideChange = { dndOverride = it },
                            customRegex = customRegex,
                            onCustomRegexChange = { customRegex = it },
                            isDark = isDark
                        )
                        3 -> StepActions(
                            autoCopy = autoCopy,
                            onAutoCopyChange = { autoCopy = it },
                            forceSound = forceSound,
                            onForceSoundChange = { forceSound = it },
                            pinSummary = pinSummary,
                            onPinSummaryChange = { pinSummary = it },
                            silentRemove = silentRemove,
                            onSilentRemoveChange = { silentRemove = it },
                            webhookUrl = webhookUrl,
                            onWebhookUrlChange = { webhookUrl = it },
                            isDark = isDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Navigation buttons ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("上一步")
                        }
                    }

                    Button(
                        onClick = {
                            if (step < 3) {
                                step++
                            } else {
                                                                // Build and confirm rule

                                val rule = AutomationRuleEntity(
                                    name = ruleName.ifBlank { "规则 ${System.currentTimeMillis()}" },
                                    selectedApps = selectedApps,
                                    triggerTemplateType = triggerType,
                                    triggerTimeMode = timeMode,
                                    triggerTimeRange = if (timeMode == "custom") """{"range":"$customTimeRange"}""" else null,
                                    triggerDndOverride = dndOverride,
                                    triggerContentRegex = customRegex.ifBlank { null },
                                    actionType = when {
                                        autoCopy -> "clipboard"
                                        forceSound -> "sound"
                                        webhookUrl.isNotBlank() -> "webhook"
                                        else -> "notification"
                                    },
                                    actionConfig = """{"url":"$webhookUrl"}""",
                                    actionAutoCopy = autoCopy,
                                    actionForceSound = forceSound,
                                    actionPinInSummary = pinSummary,
                                    actionSilentRemove = silentRemove
                                )
                                onConfirm(rule)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (step == 3) "创建规则" else "下一步",
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ── Step 1: App Source ──
@Composable
private fun StepAppSource(
    ruleName: String,
    onNameChange: (String) -> Unit,
    selectedApps: String?,
    onAppsChange: (String?) -> Unit,
    isDark: Boolean
) {
    val commonApps = listOf(
        Triple("com.tencent.mm", "微信", "💬"),
        Triple("com.tencent.mobileqq", "QQ", "🐧"),
        Triple("com.eg.android.AlipayGphone", "支付宝", "💰"),
        Triple("com.taobao.taobao", "淘宝", "🛒"),
        Triple("com.jingdong.app.mall", "京东", "📦"),
        Triple("com.ss.android.ugc.aweme", "抖音", "🎵"),
        Triple("com.sina.weibo", "微博", "📢"),
        Triple("com.netease.mail", "网易邮箱", "📧"),
        Triple("com.android.mms", "短信", "📨"),
        Triple("com.android.dialer", "电话", "📞")
    )

        val selectedSet = remember(selectedApps) {
        mutableStateListOf<String>().apply {
            selectedApps?.let { json ->
                json.removeSurrounding("[", "]").replace("\"", "").split(",").filter { it.isNotBlank() }.forEach { add(it.trim()) }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Rule name
        OutlinedTextField(
            value = ruleName,
            onValueChange = onNameChange,
            label = { Text("规则名称") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Text(
            "选择应用来源",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            "不选 = 监听所有应用",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        // App grid
        commonApps.forEach { (pkg, name, emoji) ->
            val isSelected = pkg in selectedSet
                        GlassCard(isDark = isDark, modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSelected) selectedSet.remove(pkg) else selectedSet.add(pkg)
                        val json = if (selectedSet.isEmpty()) null
                                                else "[${selectedSet.joinToString(",") { pkg -> "\"$pkg\"" }}]"
                        onAppsChange(json)
                    }
                    .then(
                        if (isSelected) Modifier.border(
                            1.5.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(14.dp)
                        ) else Modifier
                    ), cornerRadius = 14.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        name,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (isSelected) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.AddCircleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Step 2: Trigger Conditions ──
@Composable
private fun StepTriggerCondition(
    triggerType: String?,
    onTriggerTypeChange: (String?) -> Unit,
    timeMode: String,
    onTimeModeChange: (String) -> Unit,
    customTimeRange: String,
    onCustomTimeRangeChange: (String) -> Unit,
    dndOverride: Boolean,
    onDndOverrideChange: (Boolean) -> Unit,
    customRegex: String,
    onCustomRegexChange: (String) -> Unit,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Notification type templates
        Text(
            "通知类型",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        triggerTemplates.forEach { template ->
            val isSelected = triggerType == template.type
                        GlassCard(isDark = isDark, modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTriggerTypeChange(if (isSelected) null else template.type)
                    }
                    .then(
                        if (isSelected) Modifier.border(
                            1.5.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(14.dp)
                        ) else Modifier
                    ), cornerRadius = 14.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(template.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            template.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            template.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isSelected) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Custom regex input (only for keyword type)
        AnimatedVisibility(visible = triggerType == "keyword") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customRegex,
                    onValueChange = onCustomRegexChange,
                    label = { Text("正则表达式") },
                    placeholder = { Text("例: .*退订|取消.*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                // Regex test button
                if (customRegex.isNotBlank()) {
                    val isValid = try {
                        java.util.regex.Pattern.compile(customRegex)
                        true
                    } catch (_: Exception) {
                        false
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isValid) Icons.Filled.CheckCircle else Icons.Filled.Error,
                            contentDescription = null,
                            tint = if (isValid) Color(0xFF34C759) else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isValid) "正则语法正确" else "正则语法错误",
                            fontSize = 12.sp,
                            color = if (isValid) Color(0xFF34C759) else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

        // Time mode
        Text(
            "生效时段",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            timeModeOptions.forEach { option ->
                FilterChip(
                    selected = timeMode == option.mode,
                    onClick = { onTimeModeChange(option.mode) },
                    label = { Text("${option.emoji} ${option.label}", fontSize = 11.sp) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        AnimatedVisibility(visible = timeMode == "custom") {
            OutlinedTextField(
                value = customTimeRange,
                onValueChange = onCustomTimeRangeChange,
                label = { Text("时间段 (HH:MM-HH:MM)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // DND override
                GlassCard(isDark = isDark, modifier = Modifier
                .fillMaxWidth()
                .clickable { onDndOverrideChange(!dndOverride) }, cornerRadius = 14.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔕", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("免打扰时仍触发", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("检测到系统 DND 开启时仍执行规则", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = dndOverride,
                    onCheckedChange = onDndOverrideChange
                )
            }
        }
    }
}

// ── Step 3: Actions ──
@Composable
private fun StepActions(
    autoCopy: Boolean,
    onAutoCopyChange: (Boolean) -> Unit,
    forceSound: Boolean,
    onForceSoundChange: (Boolean) -> Unit,
    pinSummary: Boolean,
    onPinSummaryChange: (Boolean) -> Unit,
    silentRemove: Boolean,
    onSilentRemoveChange: (Boolean) -> Unit,
    webhookUrl: String,
    onWebhookUrlChange: (String) -> Unit,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "执行动作",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "选择触发后要执行的操作",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        actionToggles.forEach { toggle ->
            val isChecked = when (toggle.key) {
                "auto_copy" -> autoCopy
                "force_sound" -> forceSound
                "pin_summary" -> pinSummary
                "silent_remove" -> silentRemove
                "webhook" -> webhookUrl.isNotBlank()
                else -> false
            }

                        GlassCard(isDark = isDark, modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (toggle.key) {
                            "auto_copy" -> onAutoCopyChange(!autoCopy)
                            "force_sound" -> onForceSoundChange(!forceSound)
                            "pin_summary" -> onPinSummaryChange(!pinSummary)
                            "silent_remove" -> onSilentRemoveChange(!silentRemove)
                        }
                    }, cornerRadius = 14.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(toggle.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            toggle.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            toggle.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (toggle.key != "webhook") {
                        Switch(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                when (toggle.key) {
                                    "auto_copy" -> onAutoCopyChange(checked)
                                    "force_sound" -> onForceSoundChange(checked)
                                    "pin_summary" -> onPinSummaryChange(checked)
                                    "silent_remove" -> onSilentRemoveChange(checked)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Webhook URL input (expanded when webhook toggle is active)
        AnimatedVisibility(visible = webhookUrl.isNotBlank() || true) {
            OutlinedTextField(
                value = webhookUrl,
                onValueChange = onWebhookUrlChange,
                label = { Text("Webhook URL (可选)") },
                placeholder = { Text("https://your-webhook-url.com/notify") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Link, null) }
            )
        }
    }
}

// ── Helpers ──
private fun templateTypeToLabel(type: String): String = when (type) {
    "verification_code" -> "验证码"
    "amount" -> "消费"
    "messaging" -> "消息"
    "logistics" -> "物流"
    "keyword" -> "关键词"
    else -> type
}

private fun timeModeToLabel(mode: String): String = when (mode) {
    "daytime" -> "白天"
    "nighttime" -> "夜间"
    "custom" -> "自定义"
    else -> ""
}
