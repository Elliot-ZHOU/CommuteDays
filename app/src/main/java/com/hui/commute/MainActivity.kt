package com.hui.commute

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hui.commute.data.HistoryRecord
import com.hui.commute.ui.CommuteViewModel
import com.hui.commute.ui.ConfirmDeleteDialog
import com.hui.commute.ui.CounterScreen
import com.hui.commute.ui.EditHistoryDialog
import com.hui.commute.ui.HistoricalAddDialog
import com.hui.commute.ui.HistoryScreen
import com.hui.commute.ui.PortPickerDialog
import com.hui.commute.ui.theme.CommuteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommuteTheme {
                CommuteApp()
            }
        }
    }
}

private enum class DialogState {
    None,
    PortForTotal,
    PortForBorderPass,
    HistoricalAdd,
}

@Composable
private fun CommuteApp(viewModel: CommuteViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: "counter"
    val context = LocalContext.current

    var dialogState by remember { mutableStateOf(DialogState.None) }
    var editingRecord by remember { mutableStateOf<HistoryRecord?>(null) }
    var deletingRecord by remember { mutableStateOf<HistoryRecord?>(null) }

    LaunchedEffect(uiState.message) {
        val msg = uiState.message ?: return@LaunchedEffect
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "counter",
                    onClick = {
                        if (currentRoute != "counter") {
                            navController.navigate("counter") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text("计数") },
                )
                NavigationBarItem(
                    selected = currentRoute == "history",
                    onClick = {
                        if (currentRoute != "history") {
                            navController.navigate("history") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Filled.List, contentDescription = null) },
                    label = { Text("查看记录") },
                )

            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "counter",
            modifier = Modifier.padding(padding),
        ) {
            composable("counter") {
                CounterScreen(
                    totalDays = uiState.totalDays,
                    borderPassDays = uiState.borderPassDays,
                    onHistoricalAdd = { dialogState = DialogState.HistoricalAdd },
                    onIncrementTotal = { dialogState = DialogState.PortForTotal },
                    onDecrementTotal = { viewModel.decrementTotal() },
                    onIncrementBorderPass = { dialogState = DialogState.PortForBorderPass },
                    onDecrementBorderPass = { viewModel.decrementBorderPass() },
                )
            }
            composable("history") {
                HistoryScreen(
                    history = uiState.history,
                    onAdd = { dialogState = DialogState.HistoricalAdd },
                    onEdit = { editingRecord = it },
                    onDelete = { deletingRecord = it },
                )
            }
        }
    }

    when (dialogState) {
        DialogState.None -> Unit
        DialogState.PortForTotal -> {
            PortPickerDialog(
                title = "选择口岸（总天数）",
                onConfirm = { port ->
                    dialogState = DialogState.None
                    viewModel.addTodayTotal(port)
                },
                onDismiss = { dialogState = DialogState.None },
            )
        }
        DialogState.PortForBorderPass -> {
            PortPickerDialog(
                title = "选择口岸（刷关）",
                onConfirm = { port ->
                    dialogState = DialogState.None
                    viewModel.addTodayBorderPass(port)
                },
                onDismiss = { dialogState = DialogState.None },
            )
        }
        DialogState.HistoricalAdd -> {
            HistoricalAddDialog(
                onConfirm = { date, port, isBorderPass ->
                    dialogState = DialogState.None
                    viewModel.addHistorical(date, port, isBorderPass)
                },
                onDismiss = { dialogState = DialogState.None },
            )
        }
    }

    editingRecord?.let { record ->
        EditHistoryDialog(
            record = record,
            onConfirm = { date, port, isBorderPass ->
                editingRecord = null
                viewModel.updateRecord(record.id, date, port, isBorderPass)
            },
            onDismiss = { editingRecord = null },
        )
    }

    deletingRecord?.let { record ->
        ConfirmDeleteDialog(
            onConfirm = {
                deletingRecord = null
                viewModel.deleteRecord(record.id)
            },
            onDismiss = { deletingRecord = null },
        )
    }
}
