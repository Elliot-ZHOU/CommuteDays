package com.hui.commute.data

/**
 * One commute-day event. Uniqueness is by [date] (yyyy-MM-dd local calendar day).
 */
data class HistoryRecord(
    val id: Long,
    val date: String,
    val port: String,
    val isBorderPass: Boolean,
    val createdAt: Long,
)
