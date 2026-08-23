package com.apphub.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.apphub.launcher.MainViewModel
import com.apphub.launcher.domain.AppEntry
import com.apphub.launcher.domain.EntryType
import kotlinx.coroutines.launch

@Composable
fun MainScreen(vm: MainViewModel) {
    val tk = LocalWin11.current
    val entries by vm.visibleEntries.collectAsState()
    val cats by vm.categories.collectAsState()
    val activeCat by vm.activeCategory.collectAsState()
    val query by vm.query.collectAsState()
    val editing by vm.editing.collectAsState()
    val showIO by vm.showIO.collectAsState()
    val installed by vm.installed.collectAsState()
    val toast by vm.toast.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var dark by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    AppHubTheme(dark = dark) {
        Box(Modifier.fillMaxSize().background(tk.background)) {
            Column {
                // 顶部：标题 + 添加按钮 + 主题 + 导入导出
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "AppHub", color = tk.text,
                            fontSize = 20.sp, fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "所有应用 · ${entries.size}",
                            color = tk.textDim, fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = { dark = !dark }) {
                        Icon(
                            if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            null, tint = tk.textDim
                        )
                    }
                    IconButton(onClick = { vm.toggleIO(true) }) {
                        Icon(Icons.Default.ImportExport, null, tint = tk.textDim)
                    }
                    IconButton(
                        onClick = { vm.startAdd(EntryType.Web) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tk.accent)
                    ) {
                        Icon(Icons.Default.Add, null, tint = tk.accentText)
                    }
                }
                // 搜索框
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(tk.card),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        Icons.Default.Search, null,
                        modifier = Modifier.size(15.dp), tint = tk.textMute
                    )
                    Spacer(Modifier.width(6.dp))
                    BasicTextField(
                        value = query,
                        onValueChange = { vm.setQuery(it) },
                        singleLine = true,
                        cursorBrush = SolidColor(tk.accent),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = tk.text, fontSize = 13.sp
                        ),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            Box(Modifier.fillMaxWidth()) {
                                if (query.isEmpty()) Text(
                                    "搜索应用 / 网址 / 包名",
                                    color = tk.textMute, fontSize = 13.sp
                                )
                                inner()
                            }
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                }
                // 分类 chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(cats) { (c, n) ->
                        CategoryChip(
                            name = c,
                            n = n,
                            selected = c == activeCat,
                            onClick = { vm.setCategory(c) }
                        )
                    }
                }
                // 网格
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(88.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(entries, key = { it.id }) { e ->
                        AppTile(
                            entry = e,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                vm.launch(e)
                            },
                            onEdit = { vm.startEdit(e) }
                        )
                    }
                }
                // 底部栏
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(tk.accent)
                        )
                        Text(
                            "已固定 ${entries.size}",
                            color = tk.textDim, fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { vm.toggleIO(true) }) {
                        Icon(Icons.Default.ImportExport, null, tint = tk.textDim)
                    }
                }
            }

            // 添加/编辑对话框
            if (editing != null) {
                EditDialog(
                    entry = editing!!,
                    installed = installed,
                    onDismiss = vm::cancelEdit,
                    onSave = { n, t, u, p, i, c -> vm.saveDraft(n, t, u, p, i, c) },
                    onDelete = vm::deleteEditing,
                )
            }

            // 导入导出对话框
            if (showIO) {
                IODialog(
                    onClose = { vm.toggleIO(false) },
                    onExport = {
                        scope.launch {
                            val t = vm.exportText()
                            clipboard.setText(AnnotatedString(t))
                            vm.sendToast("配置已复制到剪贴板")
                        }
                    },
                    onImport = { vm.importText(it) }
                )
            }

            // Toast
            toast?.let { msg ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        Modifier
                            .shadow(8.dp, RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(tk.card)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(msg, color = tk.text, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    name: String,
    n: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tk = LocalWin11.current
    val bg = if (selected) tk.accentSoft else tk.card
    val color = if (selected) tk.accent else tk.textDim
    val weight = if (selected) FontWeight.SemiBold else FontWeight.Normal
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(name, color = color, fontSize = 12.sp, fontWeight = weight)
        Spacer(Modifier.width(5.dp))
        Text(
            "$n",
            color = if (selected) color else tk.textMute,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun AppTile(
    entry: AppEntry,
    onClick: () -> Unit,
    onEdit: () -> Unit,
) {
    val tk = LocalWin11.current
    val badgeColorBg = if (entry.type == EntryType.Native) tk.accentSoft else tk.borderStrong
    val badgeColorText = if (entry.type == EntryType.Native) tk.accent else tk.textDim
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(2.dp)
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 6.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(tk.card),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert, null,
                        modifier = Modifier.size(14.dp),
                        tint = tk.textDim
                    )
                }
            }
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 4.dp, start = 4.dp)
                    .size(height = 14.dp, width = 32.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(badgeColorBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (entry.type == EntryType.Native) "APP" else "WEB",
                    color = badgeColorText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .padding(top = 2.dp)
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(tk.card)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EntryIcon(entry = entry)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    entry.name,
                    color = tk.text,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EntryIcon(entry: AppEntry) {
    val url = entry.icon
    if (url.startsWith("http://") || url.startsWith("https://")) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        Text(
            entry.icon, fontSize = 26.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
