package com.apphub.launcher.ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.apphub.launcher.domain.AppEntry
import com.apphub.launcher.domain.Category
import com.apphub.launcher.domain.EntryType
import com.apphub.launcher.domain.InstalledApp

@Composable
fun EditDialog(
    entry: AppEntry,
    installed: List<InstalledApp>,
    onDismiss: () -> Unit,
    onSave: (String, EntryType, String, String, String, String) -> Boolean,
    onDelete: () -> Unit,
) {
    val tk = LocalWin11.current
    var type by remember(entry) { mutableStateOf(entry.type) }
    var name by remember(entry) { mutableStateOf(entry.name) }
    var url by remember(entry) { mutableStateOf(entry.url) }
    var pkg by remember(entry) { mutableStateOf(entry.pkg) }
    var icon by remember(entry) { mutableStateOf(entry.icon) }
    var category by remember(entry) { mutableStateOf(entry.category) }
    var nameErr by remember { mutableStateOf(false) }
    var pkgErr by remember { mutableStateOf(false) }
    var showInstalledPicker by remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false,
            dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color(0x47000000))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(tk.card)
                    .padding(bottom = 18.dp)
            ) {
                Column(Modifier.padding(22.dp, 18.dp, 22.dp, 4.dp)) {
                    Text(
                        if (entry.id.isBlank()) "添加应用" else "编辑应用",
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = tk.text
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (entry.id.isBlank()) "添加网站书签或安卓原生应用"
                        else "修改这个应用的资料",
                        fontSize = 12.sp, color = tk.textDim
                    )
                }
                Column(Modifier.padding(14.dp, 0.dp, 22.dp, 0.dp)) {
                    FieldLabel("类型")
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(tk.background)
                            .border(1.dp, tk.borderStrong, RoundedCornerShape(5.dp))
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SegButton("网页 URL", selected = type == EntryType.Web) {
                            type = EntryType.Web; pkgErr = false
                        }
                        SegButton("原生应用 包名", selected = type == EntryType.Native) {
                            type = EntryType.Native
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    Field("名称", name, {
                        name = it; nameErr = it.isBlank()
                    }, err = nameErr, errMsg = "请填写名称", placeholder = "例如 GitHub")

                    if (type == EntryType.Web) {
                        Field(
                            "网址", url, { url = it },
                            placeholder = "https://github.com",
                            keyboardType = KeyboardType.Uri
                        )
                    } else {
                        FieldLabel("包名")
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FieldInline(
                                modifier = Modifier.weight(1f),
                                value = pkg,
                                onValue = {
                                    pkg = it
                                    pkgErr = !validPkg(it)
                                },
                                err = pkgErr,
                                placeholder = "com.tencent.mm",
                            )
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = { showInstalledPicker = true }) {
                                Text("从已装应用选…", color = tk.accent, fontSize = 12.sp)
                            }
                        }
                        if (pkgErr) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "包名格式不正确（至少两段 . 分隔）",
                                color = tk.danger, fontSize = 11.sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "会通过 PackageManager 查询并启动该包名的主 Activity。",
                            color = tk.textMute, fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Field("图标", icon, { icon = it },
                            modifier = Modifier.weight(1f),
                            placeholder = "emoji 或图片 URL")
                        Column(Modifier.width(140.dp)) {
                            FieldLabel("分类")
                            DropdownSelect(
                                selected = category,
                                options = Category.ALL,
                                onSelect = { category = it })
                        }
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .background(tk.background.copy(alpha = 0.015f))
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (entry.id.isNotBlank()) {
                        TextButton(onClick = onDelete) {
                            Text("删除", color = tk.danger)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text("取消", color = tk.text)
                    }
                    Spacer(Modifier.width(4.dp))
                    androidx.compose.material3.Button(
                        onClick = {
                            var ok = true
                            if (name.isBlank()) { nameErr = true; ok = false }
                            if (type == EntryType.Native && !validPkg(pkg)) {
                                pkgErr = true; ok = false
                            }
                            if (ok) {
                                val saved = onSave(name, type, url, pkg, icon, category)
                                if (saved) focus.clearFocus()
                            }
                        },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tk.accent,
                            contentColor = tk.accentText
                        )
                    ) { Text("确定") }
                }
            }
        }

        if (showInstalledPicker) {
            InstalledPickerDialog(
                installed = installed,
                onPick = { app ->
                    pkg = app.pkg
                    if (name.isBlank()) name = app.label
                    showInstalledPicker = false
                },
                onClose = { showInstalledPicker = false }
            )
        }
    }
}

private fun validPkg(s: String): Boolean {
    if (s.isBlank()) return false
    val parts = s.split('.')
    if (parts.size < 2) return false
    val seg = Regex("^[A-Za-z_][A-Za-z0-9_]*$")
    return parts.all { seg.matches(it) }
}

@Composable
private fun RowScope.SegButton(label: String, selected: Boolean, onClick: () -> Unit) {
    val tk = LocalWin11.current
    val bg = if (selected) tk.card else androidx.compose.ui.graphics.Color.Transparent
    val color = if (selected) tk.text else tk.textDim
    val shadow = if (selected) Modifier.shadow(1.dp, RoundedCornerShape(3.dp)) else Modifier
    Box(
        Modifier
            .weight(1f)
            .height(28.dp)
            .padding(horizontal = 2.dp)
            .then(shadow)
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label, fontSize = 12.sp, color = color,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun FieldLabel(label: String) {
    val tk = LocalWin11.current
    Text(
        label, color = tk.textDim, fontSize = 11.sp, fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun Field(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    err: Boolean = false,
    errMsg: String = "",
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(modifier) {
        FieldLabel(label)
        FieldInline(value, onValue, err, placeholder, keyboardType)
        if (err && errMsg.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(errMsg, color = LocalWin11.current.danger, fontSize = 11.sp)
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun FieldInline(
    value: String,
    onValue: (String) -> Unit,
    err: Boolean = false,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
) {
    val tk = LocalWin11.current
    var focused by remember { mutableStateOf(false) }
    val highlight = when {
        err -> Modifier.border(1.dp, tk.danger.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
        focused -> Modifier.border(1.dp, tk.accent, RoundedCornerShape(4.dp))
        else -> Modifier
    }
    Box(
        modifier
            .fillMaxWidth()
            .height(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(tk.card)
            .then(highlight)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValue,
            singleLine = true,
            cursorBrush = SolidColor(tk.accent),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
            textStyle = androidx.compose.ui.text.TextStyle(
                color = tk.text, fontSize = 13.sp
            ),
            decorationBox = { inner ->
                Box(Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) Text(
                        placeholder,
                        color = tk.textMute, fontSize = 13.sp
                    )
                    inner()
                }
            }
        )
    }
}

@Composable
private fun DropdownSelect(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    val tk = LocalWin11.current
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(tk.card)
                .border(1.dp, tk.borderStrong, RoundedCornerShape(4.dp))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selected, color = tk.text, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("▾", color = tk.textMute, fontSize = 12.sp)
        }
        if (expanded) {
            Dialog(onDismissRequest = { expanded = false }) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tk.card)
                        .padding(vertical = 4.dp)
                        .width(160.dp)
                ) {
                    options.forEach { c ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(c); expanded = false }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                c,
                                color = if (c == selected) tk.accent else tk.text,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstalledPickerDialog(
    installed: List<InstalledApp>,
    onPick: (InstalledApp) -> Unit,
    onClose: () -> Unit,
) {
    val tk = LocalWin11.current
    var q by remember { mutableStateOf("") }
    val list = remember(installed, q) {
        val key = q.lowercase()
        if (key.isBlank()) installed else installed.filter {
            it.label.lowercase().contains(key) || it.pkg.lowercase().contains(key)
        }
    }
    Dialog(onDismissRequest = onClose) {
        Column(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(tk.card)
                .fillMaxWidth()
                .padding(16.dp)
                .height(500.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "选择已装应用", fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = tk.text,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Close, null,
                        modifier = Modifier.size(18.dp), tint = tk.textDim
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            FieldInline(value = q, onValue = { q = it },
                placeholder = "搜索应用名或包名…")
            Spacer(Modifier.height(10.dp))
            if (list.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("没有匹配", color = tk.textMute, fontSize = 13.sp)
                }
            } else {
                LazyColumn {
                    items(list) { app ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onPick(app) }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                app.label, color = tk.text, fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(app.pkg, color = tk.textMute, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
