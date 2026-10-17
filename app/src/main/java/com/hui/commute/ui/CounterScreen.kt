package com.hui.commute.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    totalDays: Int,
    borderPassDays: Int,
    onHistoricalAdd: () -> Unit,
    onIncrementTotal: () -> Unit,
    onDecrementTotal: () -> Unit,
    onIncrementBorderPass: () -> Unit,
    onDecrementBorderPass: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("跨境通勤") },
                navigationIcon = {
                    IconButton(onClick = onHistoricalAdd) {
                        Icon(Icons.Filled.Add, contentDescription = "添加历史记录")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CounterCard(
                label = "总天数",
                value = totalDays,
                onPlus = onIncrementTotal,
                onMinus = onDecrementTotal,
            )
            CounterCard(
                label = "刷关天数",
                value = borderPassDays,
                onPlus = onIncrementBorderPass,
                onMinus = onDecrementBorderPass,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "计数来自历史记录；同一天只能添加一条。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun CounterCard(
    label: String,
    value: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(onClick = onMinus) {
                    Icon(Icons.Filled.Remove, contentDescription = "减少$label")
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = value.toString(),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = onPlus) {
                    Icon(Icons.Filled.Add, contentDescription = "增加$label")
                }
            }
        }
    }
}
