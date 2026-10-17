package com.hui.commute.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hui.commute.data.HistoryRecord
import com.hui.commute.data.HistoryRepository
import com.hui.commute.data.MutationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CommuteUiState(
    val totalDays: Int = 0,
    val borderPassDays: Int = 0,
    val history: List<HistoryRecord> = emptyList(),
    val message: String? = null,
)

class CommuteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistoryRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(CommuteUiState())
    val uiState: StateFlow<CommuteUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun addTodayTotal(port: String) {
        applyMutation(repository.addToday(port, isBorderPass = false))
    }

    fun addTodayBorderPass(port: String) {
        applyMutation(repository.addToday(port, isBorderPass = true))
    }

    fun addHistorical(date: String, port: String, isBorderPass: Boolean) {
        applyMutation(repository.add(date, port, isBorderPass))
    }

    fun decrementTotal() {
        if (_uiState.value.totalDays <= 0) return
        applyMutation(repository.deleteLatestNonBorderPass(), silentNoOp = true)
    }

    fun decrementBorderPass() {
        if (_uiState.value.borderPassDays <= 0) return
        applyMutation(repository.deleteLatestBorderPass(), silentNoOp = true)
    }

    fun updateRecord(id: Long, date: String, port: String, isBorderPass: Boolean) {
        applyMutation(repository.update(id, date, port, isBorderPass))
    }

    fun deleteRecord(id: Long) {
        applyMutation(repository.delete(id), silentNoOp = true)
    }

    private fun applyMutation(result: MutationResult, silentNoOp: Boolean = false) {
        when (result) {
            MutationResult.Success -> refresh()
            MutationResult.AlreadyExists -> {
                refresh()
                _uiState.update { it.copy(message = "已添加") }
            }
            MutationResult.InvalidDate -> {
                refresh()
                _uiState.update { it.copy(message = "日期无效") }
            }
            MutationResult.NoOp -> {
                if (!silentNoOp) refresh()
            }
        }
    }

    private fun refresh() {
        val history = repository.loadAll()
        _uiState.update {
            it.copy(
                totalDays = repository.totalDays(history),
                borderPassDays = repository.borderPassDays(history),
                history = history,
            )
        }
    }
}
