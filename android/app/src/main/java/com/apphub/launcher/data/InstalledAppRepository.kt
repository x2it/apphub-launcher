package com.apphub.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Build
import com.apphub.launcher.domain.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 扫描系统里已经安装的应用 —— 方便用户直接挑一个添加，不用手打包名 */
class InstalledAppRepository(private val ctx: Context) {

    suspend fun listAll(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = ctx.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            0
        } else {
            @Suppress("DEPRECATION")
            0
        }
        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                launcherIntent,
                android.content.pm.PackageManager.ResolveInfoFlags.of(flags.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(launcherIntent, flags)
        }
        resolveInfos
            .asSequence()
            .mapNotNull { ri ->
                val activity = ri.activityInfo ?: return@mapNotNull null
                val label = ri.loadLabel(pm)?.toString() ?: activity.packageName
                InstalledApp(pkg = activity.packageName, label = label)
            }
            // 同一个包名可能出现多次（有多入口），去重
            .distinctBy { it.pkg }
            .sortedBy { it.label.lowercase() }
            .toList()
    }
}
