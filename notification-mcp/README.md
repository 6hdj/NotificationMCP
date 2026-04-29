# Notification MCP — Android 通知读取服务

> 工业级高可用安卓手机通知栏实时读取 MCP 工具，支持毫秒级捕获、全字段解析、零漏读保障、异常自动恢复。

## 项目架构

```
com.miclaw.notification/
├── NotificationMcpApp.kt          # Application 入口
├── db/                             # 数据层
│   ├── AppDatabase.kt             # Room 数据库
│   ├── NotificationDao.kt         # 通知数据 DAO
│   ├── FilterRuleDao.kt           # 过滤规则 DAO
│   └── MaskingRuleDao.kt          # 脱敏规则 DAO
├── model/                          # 数据模型
│   └── NotificationData.kt        # 通知实体 + 规则实体 + 统计
├── service/                        # 核心服务层
│   ├── NotificationListenerServiceImpl.kt  # 通知监听（核心入口）
│   ├── KeepAliveService.kt        # 前台保活服务
│   ├── WatchdogService.kt         # 守护进程（互相守护）
│   └── McpWebSocketManager.kt     # WebSocket 通信管理器
├── filter/                         # 过滤/脱敏引擎
│   ├── NotificationFilterEngine.kt # 通知过滤引擎
│   └── DataMaskingEngine.kt       # 数据脱敏引擎
├── diagnostic/                     # 一键故障排查
│   ├── DiagnosticReport.kt        # 诊断报告数据模型
│   ├── DiagnosticEngine.kt        # 六链路诊断引擎
│   └── DiagnosticTool.kt          # MCP 工具接口
├── receiver/                       # 广播接收器
│   └── BootReceiver.kt            # 开机自启
└── ui/                             # 界面层
    ├── MainActivity.kt            # 状态仪表盘
    └── DiagnosticActivity.kt      # 故障排查界面
```

## 核心能力

### 1. 通知捕获与解析
- **实时监听**：通过 `NotificationListenerService` 毫秒级捕获系统通知栏的每一条通知
- **全字段解析**：标题、内容、APP名称、包名、优先级、分类、时间、大文本、子文本、自定义Extra
- **智能去重**：基于 `notificationId + packageName + postTime` 三元组指纹去重

### 2. 高可用保障
- **三重保活**：前台服务 + WakeLock + 守护进程互相守护
- **崩溃自愈**：WatchdogService 30秒检测一次，服务死亡自动重启
- **断连补传**：WebSocket 断连期间缓存通知，重连后自动补传积压数据
- **指数退避重连**：1s → 2s → 4s → ... → 30s 上限

### 3. 智能过滤
- **五维过滤**：按包名、APP名称、关键词正则、优先级、通知分类
- **黑白名单**：内置高优先级白名单（微信/QQ/钉钉等永不过滤）和默认黑名单（系统UI/推送框架等默认过滤）
- **自定义规则**：支持运行时添加/修改/删除过滤规则

### 4. 数据脱敏
- **内置脱敏**：手机号、身份证号、银行卡号、邮箱、验证码、密码、IP地址
- **自定义规则**：正则匹配 + 替换模板
- **转发前脱敏**：在通知发送到电脑端之前完成脱敏处理

### 5. 一键故障排查
- **六大诊断链路**：权限校验 → 服务状态 → 系统拦截 → 连接状态 → 资源状态 → 日志分析
- **22 项检查**：覆盖 MIUI/HyperOS 特有权限、Doze 模式、电池策略、内存压力等
- **一键修复**：自动跳转到对应的系统设置页面
- **诊断历史**：保留最近 10 次诊断记录

### 6. MCP 工具接口
暴露给电脑端 Miclaw 的工具列表：

| 工具名 | 说明 |
|--------|------|
| `get_notifications` | 获取通知列表（支持关键词/包名/时间范围搜索） |
| `get_notification_stats` | 获取今日统计（接收/转发/过滤/异常） |
| `get_filter_rules` | 获取过滤规则 |
| `add_filter_rule` | 添加过滤规则 |
| `delete_filter_rule` | 删除过滤规则 |
| `get_masking_rules` | 获取脱敏规则 |
| `add_masking_rule` | 添加脱敏规则 |
| `get_logs` | 获取运行日志 |
| `trigger_action` | 触发自定义动作 |
| `run_diagnostic` | 一键故障排查 |
| `auto_fix` | 一键自动修复 |
| `get_diagnostic_history` | 诊断历史 |

## 构建与安装

### 环境要求
- Android Studio Hedgehog+
- JDK 17
- Android SDK 35
- Kotlin 1.9+

### 构建步骤
```bash
# 克隆项目
git clone <repo-url>
cd notification-mcp

# 构建 Debug APK
./gradlew assembleDebug

# 安装到设备
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 首次配置
1. 安装 APK 后打开应用
2. 点击「开启通知使用权」→ 找到「通知读取」→ 开启
3. 确保应用在电池优化白名单中
4. 在电脑端 Miclaw 的 MCP 配置中添加本服务的 WebSocket 地址

## 技术栈

| 组件 | 技术选型 | 版本 |
|------|---------|------|
| 语言 | Kotlin | 1.9+ |
| 最低版本 | Android 11 (API 30) | - |
| 目标版本 | Android 15 (API 35) | - |
| 数据库 | Room | 2.6.1 |
| WebSocket | OkHttp | 4.12.0 |
| JSON | Gson | 2.11.0 |
| 协程 | Kotlin Coroutines | 1.9.0 |
| 异步 | Flow + StateFlow | - |

## 许可证

MIT License
