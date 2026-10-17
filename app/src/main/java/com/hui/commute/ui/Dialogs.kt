package com.hui.commute.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hui.commute.data.HistoryRecord
import com.hui.commute.data.HistoryRepository
import com.hui.commute.data.Ports
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DisplayDateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")

@Composable
fun PortPickerDialog(
    title: String = "选择口岸",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selected by remember { mutableStateOf(Ports.ALL.first()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Ports.ALL.forEach { port ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selected == port,
                                onClick = { selected = port },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selected == port,
                            onClick = { selected = port },
                        )
                        Text(port, Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
fun HistoricalAddDialog(
    onConfirm: (date: String, port: String, isBorderPass: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var date by remember { mutableStateOf(HistoryRepository.todayString()) }
    var selectedPort by remember { mutableStateOf(Ports.ALL.first()) }
    var isBorderPass by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加历史记录") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("日期", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = formatDisplayDate(date),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDatePicker(context, date) { picked -> date = picked }
                        }
                        .padding(vertical = 4.dp),
                )
                Text("口岸", style = MaterialTheme.typography.labelLarge)
                Ports.ALL.forEach { port ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedPort == port,
                                onClick = { selectedPort = port },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedPort == port,
                            onClick = { selectedPort = port },
                        )
                        Text(port, Modifier.padding(start = 8.dp))
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { isBorderPass = !isBorderPass }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = isBorderPass,
                        onCheckedChange = { isBorderPass = it },
                    )
                    Text("刷关", Modifier.padding(start = 4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(date, selectedPort, isBorderPass) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
fun EditHistoryDialog(
    record: HistoryRecord,
    onConfirm: (date: String, port: String, isBorderPass: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var date by remember { mutableStateOf(record.date) }
    var selectedPort by remember { mutableStateOf(record.port) }
    var isBorderPass by remember { mutableStateOf(record.isBorderPass) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑记录") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("日期", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = formatDisplayDate(date),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDatePicker(context, date) { picked -> date = picked }
                        }
                        .padding(vertical = 4.dp),
                )
                Text("口岸", style = MaterialTheme.typography.labelLarge)
                Ports.ALL.forEach { port ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedPort == port,
                                onClick = { selectedPort = port },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedPort == port,
                            onClick = { selectedPort = port },
                        )
                        Text(port, Modifier.padding(start = 8.dp))
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { isBorderPass = !isBorderPass }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = isBorderPass,
                        onCheckedChange = { isBorderPass = it },
                    )
                    Text("刷关", Modifier.padding(start = 4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(date, selectedPort, isBorderPass) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除记录") },
        text = { Text("确定删除这条记录吗？") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

fun formatDisplayDate(isoDate: String): String {
    return runCatching {
        LocalDate.parse(isoDate, HistoryRepository.DATE_FMT).format(DisplayDateFmt)
    }.getOrDefault(isoDate)
}

private fun showDatePicker(
    context: android.content.Context,
    currentIso: String,
    onPicked: (String) -> Unit,
) {
    val current = runCatching { LocalDate.parse(currentIso, HistoryRepository.DATE_FMT) }
        .getOrElse { LocalDate.now() }
    val today = LocalDate.now()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val picked = LocalDate.of(year, month + 1, dayOfMonth)
            if (!picked.isAfter(today)) {
                onPicked(picked.format(HistoryRepository.DATE_FMT))
            }
        },
        current.year,
        current.monthValue - 1,
        current.dayOfMonth,
    ).apply {
        datePicker.maxDate = System.currentTimeMillis()
    }.show()
}
