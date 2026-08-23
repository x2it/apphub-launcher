package com.apphub.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch

@Composable
fun IODialog(
    onClose: () -> Unit,
    onExport: suspend () -> Unit,
    onImport: (String) -> Unit,
) {
    val tk = LocalWin11.current
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0x47000000))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(tk.card)
                    .padding(bottom = 12.dp)
            ) {
                Column(Modifier.padding(22.dp, 18.dp, 22.dp, 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "导出 / 导入", fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold, color = tk.text
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("备份或迁移你的应用列表", fontSize = 12.sp, color = tk.textDim)
                        }
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.padding(end = (-4).dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = tk.textDim)
                        }
                    }
                }

                Column(Modifier.padding(horizontal = 22.dp)) {
                    Text(
                        "当前配置（复制即可导出，粘贴即可导入）",
                        color = tk.textDim, fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(tk.card)
                            .border(1.dp, tk.borderStrong, RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = text,
                            onValueChange = { text = it },
                            cursorBrush = SolidColor(tk.accent),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = tk.text, fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.fillMaxSize(),
                            decorationBox = { inner ->
                                Box(Modifier.fillMaxSize()) {
                                    if (text.isEmpty()) {
                                        Text(
                                            "点『导出并复制』加载当前配置；或在此粘贴 JSON 后点『导入』",
                                            color = tk.textMute, fontSize = 12.sp
                                        )
                                    }
                                    inner()
                                }
                            }
                        )
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .padding(horizontal = 22.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onClose) { Text("关闭", color = tk.text) }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { onImport(text) }) {
                        Text("导入", color = tk.text)
                    }
                    androidx.compose.material3.Button(
                        onClick = {
                            scope.launch { onExport() }
                        },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tk.accent,
                            contentColor = tk.accentText
                        )
                    ) {
                        Text("导出并复制")
                    }
                }
            }
        }
    }
}
