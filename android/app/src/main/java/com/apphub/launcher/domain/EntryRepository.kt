package com.apphub.launcher.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.apphub.launcher.data.EntryDao
import com.apphub.launcher.data.EntryEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.json.JSONArray
import org.json.JSONObject

class EntryRepository(
    private val dao: EntryDao,
    private val ctx: Context,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun entries(): Flow<List<AppEntry>> =
        dao.observeAll().mapLatest { list -> list.map { it.toDomain() } }

    suspend fun getAll(): List<AppEntry> = dao.getAll().map { it.toDomain() }

    suspend fun upsert(entry: AppEntry) {
        dao.insert(EntryEntity.from(entry))
    }

    suspend fun delete(id: String) = dao.delete(id)

    suspend fun replaceAll(list: List<AppEntry>) {
        dao.clearAll()
        if (list.isNotEmpty()) dao.insertAll(list.map { EntryEntity.from(it) })
    }

    /** 启动条目：native 走 startActivity，web 走 ACTION_VIEW */
    fun launch(entry: AppEntry) {
        when (entry.type) {
            EntryType.Native -> {
                val intent = if (entry.pkg.isNotBlank()) {
                    ctx.packageManager.getLaunchIntentForPackage(entry.pkg)
                } else null
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ctx.startActivity(intent)
                }
            }
            EntryType.Web -> {
                val url = entry.url.ifBlank { return }
                val u = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(u))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
            }
        }
    }

    // ---- 导入导出 JSON（与网页版 localStorage 结构完全一致） ----
    fun toJson(list: List<AppEntry>): String {
        val arr = JSONArray()
        list.forEach { e ->
            val o = JSONObject()
            o.put("id", e.id)
            o.put("name", e.name)
            o.put("type", e.type.value)
            o.put("url", e.url)
            o.put("pkg", e.pkg)
            o.put("icon", e.icon)
            o.put("category", e.category)
            arr.put(o)
        }
        return arr.toString(2)
    }

    fun fromJson(text: String): List<AppEntry>? = runCatching {
        val arr = JSONArray(text)
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val type = EntryType.from(o.optString("type", "web"))
            AppEntry(
                id = o.optString("id").ifBlank { "imp-${System.nanoTime()}-$i" },
                name = o.optString("name"),
                type = type,
                url = o.optString("url"),
                pkg = o.optString("pkg"),
                icon = o.optString("icon", "📦").ifBlank { "📦" },
                category = o.optString("category", Category.DEFAULT).ifBlank { Category.DEFAULT },
                order = i,
            )
        }
    }.getOrNull()
}
