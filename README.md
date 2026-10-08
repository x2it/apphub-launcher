# AppHub Launcher · 知行工作室

> A Win11-style Android launcher that manages **native apps** and **web bookmarks** in one place.
>
> 一个 Windows 11 风格的 Android 启动器，在同一界面统一管理原生应用与网页书签。

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Platform: Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg)](#)
[![Min SDK: 26](https://img.shields.io/badge/Min%20SDK-26-green.svg)](#)
[![Kotlin 2.0](https://img.shields.io/badge/Kotlin-2.0-7F52FF.svg)](#)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10-4285F4.svg)](#)

<img src="https://raw.githubusercontent.com/x2it/apphub-launcher/main/banner.png" alt="AppHub 启动器 · AppHub Launcher" width="100%">

---

## ✨ 特性 / Features

| 特性 | 说明 |
|------|------|
| 🪟 **Win11 风格 UI** | Mica-like 卡片、细腻阴影、圆角、分割线；支持浅色 / 深色主题切换 |
| 🧭 **搜索** | 按应用名、URL、包名实时过滤 |
| 🏷 **分类** | 常用 / 工作 / 开发 / 社交 / 媒体 / 其他，自适应 Chips + 数量徽章 |
| 📱 **两种条目** | 原生 APP（通过包名 + `PackageManager` 启动主 Activity） + Web 书签（默认浏览器打开） |
| 📥 **已装应用选择器** | 添加原生应用时，可直接扫描手机上已装的应用并自动填入包名 |
| 💾 **Room 持久化** | 重启后数据不丢失 |
| 📤 **JSON 导入导出** | 一键复制/粘贴完整配置，方便换机或备份 |
| 🔔 **Toast + 触觉反馈** | 操作反馈细腻 |
| 🎯 **Grid 自适应** | 不同屏幕尺寸下自适应网格大小 |
| 🧩 **Web 原型** | 仓库内含 [prototype.html](prototype.html)，浏览器直接打开即可预览交互 |

---

## 🖼 预览 / Preview

> 浏览器可先打开 `prototype.html` 体验完整交互原型。
> 安装 APK 后在真机上即可看到与原型一致的 Win11 风格界面。

```
┌─────────────────────────────────────────────┐
│  AppHub                          🔆  ↕️  ➕   │
│  所有应用 · 12                                  │
├─────────────────────────────────────────────┤
│  🔍 搜索应用 / 网址 / 包名                       │
├─────────────────────────────────────────────┤
│  [全部(12)] [常用(3)] [工作(2)] [开发(3)] ... │
├─────────────────────────────────────────────┤
│  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐       │
│  │WEB │ │APP │ │WEB │ │APP │ │WEB │       │
│  │ 🐙 │ │ 💬│ │ 🎵│ │ 📷│ │ 📧│       │
│  │GitHub│微信 │Spotify│相机 │Gmail │       │
│  └────┘ └────┘ └────┘ └────┘ └────┘       │
│  ...                                          │
├─────────────────────────────────────────────┤
│  ● 已固定 12                       ↕️         │
└─────────────────────────────────────────────┘
```

---

## 📲 下载安装 / Installation

### 方式一：直接下载 APK（推荐）

从仓库 `release/` 目录下载最新 debug 安装包：

```
release/AppHub-debug.apk   (16.8 MB)
```

SHA256：`2b70a6445f57a2a01b103d3a7d40e2dbd42f8e5162af3ee01657846bb87435e9`

传到手机后点击 APK 文件即可安装；系统若提示"未知来源"，请在设置中允许对应来源。

### 方式二：ADB 安装

```bash
adb install -r release/AppHub-debug.apk
```

### 方式三：自行构建

详见下方 [🔨 本地构建](#-本地构建--build-from-source) 章节。

---

## 🛠 用法 / Usage

### 添加条目

1. 点击右上角 `➕` 按钮
2. 选择 **类型**：
   - `网页 URL` → 填网址（自动补齐 `https://`）
   - `原生应用 包名` → 填包名（如 `com.tencent.mm`），或点 `从已装应用选…` 扫描选择
3. 填 **名称**、**图标**（emoji 或图片 URL）、**分类** → `确定`

### 编辑 / 删除

点击 Tile 右上角 `⋮` 按钮，进入编辑弹窗后可修改或删除。

### 分类 & 搜索

- 顶部 Chips 按分类筛选
- 搜索框支持按**名称 / URL / 包名**实时过滤

### 导入导出

点右上角 `↕️` 按钮 → `导出并复制`（把 JSON 复制到剪贴板备份）→ 或粘贴 JSON 后点 `导入` 还原。

---

## 🧱 架构 / Architecture

```
apphub-launcher/
├── android/                         # Android 工程（Kotlin + Jetpack Compose）
│   ├── app/
│   │   └── src/main/java/com/apphub/launcher/
│   │       ├── MainActivity.kt      # 入口 Activity
│   │       ├── MainViewModel.kt     # 状态 + 业务逻辑
│   │       ├── domain/              # 领域模型（AppEntry / EntryType / InstalledApp / Repository）
│   │       ├── data/                # 数据层（Room 数据库 + InstalledAppRepository）
│   │       └── ui/                  # UI 层（MainScreen / EditDialog / IODialog / Theme）
│   ├── build.gradle                 # 根 Gradle（Groovy，buildscript classpath 方式，代理友好）
│   ├── settings.gradle.kts          # 仓库显式 Maven URL，兼容沙箱代理
│   └── gradle/wrapper/
├── prototype.html                   # Web 原型（浏览器直接打开）
├── release/
│   └── AppHub-debug.apk             # 已构建 debug 安装包
├── LICENSE                          # MIT License © 知行工作室
├── README.md
└── .gitignore
```

### 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Kotlin 2.0.21 |
| UI | Jetpack Compose (BOM 2024.10.01) |
| 持久化 | Room 2.6.1 (KSP 生成) |
| 启动 | `PackageManager.getLaunchIntentForPackage(pkg)` 原生 / `Intent.ACTION_VIEW` 网页 |
| 异步 | Kotlin Coroutines 1.9.0 + Flow + StateFlow / SharedFlow |
| 图片 | Coil 2.7.0 |
| 构建 | Gradle 8.14 + AGP 8.5.2 + KSP 2.0.21-1.0.27 |
| JDK | Java 17 |

---

## 🔨 本地构建 / Build from Source

### 前置要求

- JDK 17
- Android SDK（`platforms;android-34`、`build-tools;34.0.0`、platform-tools）
- 代理：访问 `dl.google.com` / `maven.google.com` 需要可使用的 HTTP 代理（沙箱环境默认 `127.0.0.1:18080`）

### 配置

1. 在 `android/local.properties` 里填 SDK 路径：
   ```
   sdk.dir=/opt/android-sdk
   ```
2. （如需）设置 Gradle 代理：
   ```bash
   export GRADLE_OPTS=" \
     -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=18080 \
     -Dhttp.proxyHost=127.0.0.1  -Dhttp.proxyPort=18080 \
     -Dorg.gradle.java.home=$JAVA_17_HOME"
   ```

### 构建 Debug APK

```bash
cd android
gradle --no-daemon assembleDebug \
  -Dorg.gradle.java.home=/path/to/jdk17 \
  -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=18080 \
  -Dhttp.proxyHost=127.0.0.1  -Dhttp.proxyPort=18080
```

产物：

```
android/app/build/outputs/apk/debug/app-debug.apk
```

### 已知的构建注意事项

1. **AGP 版本**：使用 **8.5.2**（8.7.x 在当前镜像不可用），与 Kotlin 2.0.21 + Gradle 8.14 兼容。
2. **Compose Compiler**：Kotlin 2.0 起启用 compose 需要 `org.jetbrains.kotlin.plugin.compose`，已通过 buildscript classpath 加载。
3. **Groovy 构建脚本**：根 `build.gradle` 与 `app/build.gradle` 使用 Groovy（不使用 Kotlin DSL），原因是 `apply plugin:` 方式在子模块中需要预编译脚本插件才能生成访问器，Groovy 的动态调用更适合需要通过代理解决依赖的沙箱场景。
4. **JDK 17**：AGP 8.5 不支持 JDK 21+，构建时需显式 `-Dorg.gradle.java.home=...` 指定 JDK 17。

---

## 🧪 已知限制 / Limitations

- 初次发布是 debug 签名版（`release/AppHub-debug.apk`），如需分发给他人建议生成 release keystore 并签名。
- `QUERY_ALL_PACKAGES` 权限用于扫描已装应用。上架 Google Play 需要提交使用说明（或改用 `<queries>` 指定具体包）。
- 尚未注册 HOME category，不作为默认桌面使用。要作为启动器，在 `AndroidManifest.xml` 中取消对应两行注释即可。
- Launcher icon 使用默认系统主题的图标。欢迎 PR 补充 Win11 风格资源。

---

## 🤝 贡献 / Contributing

欢迎 Issue / PR！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -am 'feat: xxx'`)
4. 推送到分支 (`git push origin feature/xxx`)
5. 开启 Pull Request

提交前请：
- 本地执行 `assembleDebug` 确保能通过构建
- 新功能加一行说明到本 README

---

## 📜 开源协议 / License

本项目基于 **MIT License** 开源，版权所有 © **2026 知行工作室 (Zhixing Studio)**。

完整协议详见 [LICENSE](LICENSE) 文件。

---

<p align="center">
  <b>Made with ♥ by 知行工作室</b>
</p>

---

[MIT](LICENSE) © 2026 知行工作室 Zhixing Studio · [https://w3b.pub/](https://w3b.pub/) · support@w3b.pub
