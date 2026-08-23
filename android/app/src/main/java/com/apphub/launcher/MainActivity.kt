package com.apphub.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.apphub.launcher.data.AppDatabase
import com.apphub.launcher.data.InstalledAppRepository
import com.apphub.launcher.domain.AppEntry
import com.apphub.launcher.domain.Category
import com.apphub.launcher.domain.EntryRepository
import com.apphub.launcher.domain.EntryType
import com.apphub.launcher.ui.MainScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels {
        val db = AppDatabase.get(this)
        MainViewModelFactory(
            repo = EntryRepository(dao = db.entryDao(), ctx = this),
            installed = InstalledAppRepository(this),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 首次启动：如果数据库空，塞一批跟网页原型一致的种子数据
        lifecycleScope.launch { seedIfEmpty() }

        setContent { MainScreen(vm) }
    }

    private suspend fun seedIfEmpty() {
        val dao = AppDatabase.get(this).entryDao()
        if (dao.getAll().isNotEmpty()) return
        val seeds = listOf(
            AppEntry(id = "seed-1", name = "GitHub",
                type = EntryType.Web, url = "https://github.com", icon = "\uD83D\uDC19", category = "开发"),
            AppEntry(id = "seed-2", name = "Gmail",
                type = EntryType.Web, url = "https://mail.google.com", icon = "✉️", category = "工作"),
            AppEntry(id = "seed-3", name = "ChatGPT",
                type = EntryType.Web, url = "https://chat.openai.com", icon = "\uD83E\uDD16", category = Category.DEFAULT),
            AppEntry(id = "seed-4", name = "YouTube",
                type = EntryType.Web, url = "https://youtube.com", icon = "▶️", category = "媒体"),
            AppEntry(id = "seed-5", name = "微信",
                type = EntryType.Native, pkg = "com.tencent.mm", icon = "\uD83D\uDCAC", category = "社交"),
            AppEntry(id = "seed-6", name = "Notion",
                type = EntryType.Web, url = "https://notion.so", icon = "\uD83D\uDDD2", category = "工作"),
            AppEntry(id = "seed-7", name = "Vercel",
                type = EntryType.Web, url = "https://vercel.com", icon = "▲", category = "开发"),
            AppEntry(id = "seed-8", name = "Spotify",
                type = EntryType.Native, pkg = "com.spotify.music", icon = "\uD83C\uDFB5", category = "媒体"),
            AppEntry(id = "seed-9", name = "支付宝",
                type = EntryType.Native, pkg = "com.eg.android.AlipayGphone", icon = "\uD83D\uDCB0", category = Category.DEFAULT),
            AppEntry(id = "seed-10", name = "小红书",
                type = EntryType.Native, pkg = "com.xingin.xhs", icon = "\uD83D\uDCD5", category = "媒体"),
            AppEntry(id = "seed-11", name = "电话",
                type = EntryType.Native, pkg = "com.android.dialer", icon = "\uD83D\uDCDE", category = Category.DEFAULT),
            AppEntry(id = "seed-12", name = "Chrome",
                type = EntryType.Native, pkg = "com.android.chrome", icon = "\uD83C\uDF10", category = Category.DEFAULT),
        )
        seeds.forEach { dao.insert(com.apphub.launcher.data.EntryEntity.from(it)) }
    }
}
