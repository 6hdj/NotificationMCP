```markdown
# 📱 Notification MCP

> 让 AI 助手直接读取你手机上的通知 —— 基于 [MCP 协议](https://modelcontextprotocol.io/) 的 Android 通知智能中枢。

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-30%2B-brightgreen.svg)](https://developer.android.com/about/versions/11)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue.svg)](https://kotlinlang.org)
[![MCP](https://img.shields.io/badge/MCP-2024--11--05-orange.svg)](https://modelcontextprotocol.io)

## 这是什么？

Notification MCP 在你的 Android 手机上运行一个本地 MCP 服务器，让电脑端的 AI 助手（如 MiClaw、Claude Desktop 等任何支持 MCP 协议的客户端）可以直接用自然语言查询手机通知。

**不再需要手动翻手机找验证码、查快递、看转账记录** —— 直接问 AI 就行。

```
你：手机上有验证码吗？
AI：[自动调用 search_notifications(keyword="验证码")]
AI：找到 2 条验证码通知：
    1. 中国工商银行 - 验证码 837214，5分钟内有效
    2. 微信 - 验证码 591638，用于登录验证
```

## ✨ 功能特性

| 特性 | 说明 |
|------|------|
| 🔍 **智能搜索** | 按关键词、包名、时间范围搜索通知，毫秒级响应 |
| 🛡️ **隐私脱敏** | 手机号、身份证、银行卡、验证码等敏感信息自动脱敏 |
| 🎯 **五维过滤** | 包名黑白名单 + 关键词正则 + 优先级 + 通知分类 |
| 🔧 **一键诊断** | 六大诊断链路、22 项自动检查，一键修复 MIUI/HyperOS 问题 |
| 🔄 **高可用** | 三重保活 + 崩溃自愈 + 断连补传，7×24 小时不间断 |
| 📊 **运行统计** | 实时查看通知接收、转发、过滤、异常数据 |

## 📲 安装与配置

### 第一步：下载安装

从 [Releases](../../releases) 页面下载最新的 `app-debug.apk`，传到手机上安装。

> ⚠️ 安装时系统可能提示"未知来源"，这是正常的（因为不是从应用商店下载的），点击"仍然安装"即可。

### 第二步：开启通知使用权

1. 打开 Notification MCP 应用
2. 点击首页的 **「开启通知使用权」** 按钮
3. 在系统设置中找到 **「通知读取」**（或 Notification MCP）
4. 开启开关，确认授权

> 这个权限是必须的，没有它 App 无法读取通知栏内容。

### 第三步：关闭电池优化（重要！）

Android 系统会在后台杀掉高耗电应用，必须把 Notification MCP 加入白名单：

**小米/红米（MIUI / HyperOS）：**
1. 设置 → 电池 → 应用智能省电
2. 找到「通知读取」→ 选择 **「无限制」**
3. 设置 → 应用设置 → 应用管理 → 通知读取 → 省电策略 → **「无限制」**
4. 设置 → 应用设置 → 应用管理 → 通知读取 → 自启动 → **开启**

**华为/荣耀（HarmonyOS）：**
1. 设置 → 电池 → 启动管理
2. 找到「通知读取」→ 关闭自动管理 → 手动设置：允许自启动、允许关联启动、允许后台活动

**OPPO/realme（ColorOS）：**
1. 设置 → 电池 → 更多电池设置
2. 开启「允许应用在后台运行」
3. 设置 → 应用管理 → 通知读取 → 省电策略 → 允许后台运行

**vivo/iQOO（OriginOS）：**
1. 设置 → 电池 → 后台耗电管理
2. 找到「通知读取」→ 允许后台高耗电

**原生 Android / 三星（One UI）：**
1. 设置 → 电池 → 电池优化
2. 找到「通知读取」→ 选择 **「不优化」**

### 第四步：连接 AI 客户端

App 启动后会在手机本地运行一个 MCP 服务器（端口 8765）。在你的 AI 客户端中添加以下 MCP 配置：

```json
{
  "mcpServers": {
    "notification": {
      "type": "http",
      "url": "http://127.0.0.1:8765/mcp"
    }
  }
}
```

**不同客户端的配置方式：**

| 客户端 | 配置位置 |
|--------|----------|
| **MiClaw（小米 AI 助手）** | 对手机说"配置本地 MCP 服务器，地址是 http://127.0.0.1:8765/mcp，名称叫通知读取" |
| **Claude Desktop** | 设置 → MCP Servers → Edit Config → 粘贴上面的 JSON |
| **Cursor** | Settings → MCP → Add new global MCP server → 粘贴上面的 JSON |
| **其他 MCP 客户端** | 找到 MCP 服务器配置入口，添加 HTTP 类型，地址填 `http://127.0.0.1:8765/mcp` |

> 💡 **注意**：手机和电脑需要在同一局域网内，或者 AI 客户端直接运行在手机上。`127.0.0.1` 表示本机，如果 AI 客户端在电脑上，需要把手机的局域网 IP（如 `192.168.1.100`）替换 `127.0.0.1`。

### 第五步：验证连接

在 AI 客户端中输入：

```
帮我查一下手机上最近的通知
```

如果 AI 能返回通知列表，说明配置成功 🎉

## 🛠️ MCP 工具列表

连接成功后，AI 可以调用以下工具：

| 工具 | 参数 | 说明 |
|------|------|------|
| `search_notifications` | `keyword`, `hours`(默认24), `limit`(默认20) | 按关键词搜索通知 |
| `get_notifications` | `limit`(默认50), `package_name` | 获取通知列表 |
| `get_notification_stats` | 无 | 今日统计（接收/转发/过滤/异常） |
| `get_filter_rules` | 无 | 查看过滤规则 |
| `add_filter_rule` | `package_name`, `action`, `reason` | 添加过滤规则 |
| `delete_filter_rule` | `rule_id` | 删除过滤规则 |
| `get_masking_rules` | 无 | 查看脱敏规则 |
| `add_masking_rule` | `pattern`, `replacement` | 添加脱敏规则 |
| `run_diagnostic` | 无 | 一键故障排查 |
| `auto_fix` | 无 | 一键自动修复 |
| `get_diagnostic_history` | 无 | 查看诊断历史 |
| `get_logs` | `limit` | 获取运行日志 |

## 🏗️ 项目架构

```
com.miclaw.notification/
├── NotificationMcpApp.kt          # Application 入口
├── server/LocalMcpServer.kt       # MCP 服务器（Streamable HTTP 协议）
├── service/
│   ├── NotificationListenerServiceImpl.kt  # 通知监听（核心入口）
│   ├── KeepAliveService.kt        # 前台保活服务
│   ├── WatchdogService.kt         # 守护进程（互相守护）
│   └── McpWebSocketManager.kt     # WebSocket 通信管理器
├── filter/
│   ├── NotificationFilterEngine.kt # 通知过滤引擎
│   └── DataMaskingEngine.kt       # 数据脱敏引擎
├── diagnostic/
│   ├── DiagnosticEngine.kt        # 六链路诊断引擎
│   ├── DiagnosticReport.kt        # 诊断报告数据模型
│   └── DiagnosticTool.kt          # MCP 工具接口
├── db/                             # Room 数据库
├── model/                          # 数据模型
├── receiver/BootReceiver.kt        # 开机自启
└── ui/                             # 界面层
    ├── MainActivity.kt            # 状态仪表盘
    └── DiagnosticActivity.kt      # 故障排查界面
```

## 🔧 技术栈

| 组件 | 技术选型 | 版本 |
|------|---------|------|
| 语言 | Kotlin | 1.9+ |
| 最低版本 | Android 11 (API 30) | - |
| 目标版本 | Android 15 (API 35) | - |
| 数据库 | Room (SQLite) | 2.6.1 |
| 网络通信 | OkHttp WebSocket | 4.12.0 |
| JSON 处理 | Gson | 2.11.0 |
| 协程 | Kotlin Coroutines | 1.9.0 |
| 协议 | MCP Streamable HTTP (JSON-RPC 2.0) | 2024-11-05 |

## 📊 项目规模

- **源文件**：19 个 Kotlin 文件
- **代码量**：3,712 行（不含资源文件和构建配置）
- **模块数**：8 个功能模块
- **APK 大小**：约 14.5 MB（Debug 版本）

## ❓ 常见问题

### Q: 为什么通知读取不到？
A: 最常见的原因是没有开启通知使用权，或者电池优化把 App 杀了。按照上面的配置步骤检查一遍。

### Q: 电脑连不上手机的 MCP 服务器？
A: 确保手机和电脑在同一局域网内，把 MCP 地址中的 `127.0.0.1` 替换为手机的局域网 IP（在手机设置 → WLAN → 点击已连接的网络 → 查看 IP 地址）。

### Q: 小米手机通知总是断？
A: MIUI/HyperOS 对后台限制很严格，务必按照上面"关闭电池优化"部分的步骤，把省电策略设为「无限制」并开启自启动。

### Q: 支持哪些 Android 版本？
A: Android 11 (API 30) 及以上，覆盖市面上 95%+ 的在用 Android 设备。

### Q: 通知数据安全吗？
A: 所有数据存储在手机本地数据库中，不会上传到任何云端。转发前会自动对敏感信息（手机号、身份证、银行卡等）进行脱敏处理。

## 📄 许可证

[MIT License](LICENSE) - 自由使用，自由修改，自由分发。
```
