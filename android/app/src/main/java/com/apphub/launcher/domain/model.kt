package com.apphub.launcher.domain

/** 条目类型：网页书签 / 原生应用 */
enum class EntryType(val value: String) {
    Web("web"),
    Native("native");
    companion object {
        fun from(v: String?) = when (v) {
            "native" -> Native
            else -> Web
        }
    }
}

/** 与网页原型对齐的领域模型：应用条目 */
data class AppEntry(
    val id: String,
    val name: String,
    val type: EntryType,
    val url: String = "",
    val pkg: String = "",
    val icon: String = "📦",
    val category: String = Category.DEFAULT,
    val order: Int = 0,
)

object Category {
    const val DEFAULT = "常用"
    val ALL = listOf("常用", "工作", "开发", "社交", "媒体", "其他")
}

/** 安装在手机里的应用（扫描 PackageManager 得到） */
data class InstalledApp(
    val pkg: String,
    val label: String,
)
