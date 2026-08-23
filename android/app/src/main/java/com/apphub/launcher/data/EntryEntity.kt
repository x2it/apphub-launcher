package com.apphub.launcher.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.apphub.launcher.domain.AppEntry
import com.apphub.launcher.domain.EntryType

@Entity(tableName = "pinned_entries")
data class EntryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,     // "web" | "native"
    val url: String,
    val pkg: String,
    val icon: String,
    val category: String,
    val orderIdx: Int,
) {
    fun toDomain() = AppEntry(
        id = id,
        name = name,
        type = EntryType.from(type),
        url = url,
        pkg = pkg,
        icon = icon,
        category = category,
        order = orderIdx,
    )

    companion object {
        fun from(e: AppEntry) = EntryEntity(
            id = e.id,
            name = e.name,
            type = e.type.value,
            url = e.url,
            pkg = e.pkg,
            icon = e.icon,
            category = e.category,
            orderIdx = e.order,
        )
    }
}
