package com.hui.commute.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

sealed class MutationResult {
    data object Success : MutationResult()
    data object AlreadyExists : MutationResult()
    data object NoOp : MutationResult()
    data object InvalidDate : MutationResult()
}

class HistoryRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val idSeq = AtomicLong(prefs.getLong(KEY_NEXT_ID, 1L))

    fun loadAll(): List<HistoryRecord> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return runCatching { parse(raw) }.getOrElse { emptyList() }
            .sortedWith(compareByDescending<HistoryRecord> { it.date }.thenByDescending { it.createdAt })
    }

    fun totalDays(records: List<HistoryRecord> = loadAll()): Int = records.size

    fun borderPassDays(records: List<HistoryRecord> = loadAll()): Int =
        records.count { it.isBorderPass }

    fun addToday(port: String, isBorderPass: Boolean): MutationResult {
        val today = LocalDate.now().format(DATE_FMT)
        return add(today, port, isBorderPass)
    }

    fun add(date: String, port: String, isBorderPass: Boolean): MutationResult {
        if (!isValidSelectableDate(date)) return MutationResult.InvalidDate
        val records = loadAll().toMutableList()
        if (records.any { it.date == date }) return MutationResult.AlreadyExists
        val now = System.currentTimeMillis()
        records.add(
            HistoryRecord(
                id = idSeq.getAndIncrement(),
                date = date,
                port = port,
                isBorderPass = isBorderPass,
                createdAt = now,
            )
        )
        persist(records)
        return MutationResult.Success
    }

    fun update(
        id: Long,
        date: String,
        port: String,
        isBorderPass: Boolean,
    ): MutationResult {
        if (!isValidSelectableDate(date)) return MutationResult.InvalidDate
        val records = loadAll().toMutableList()
        val index = records.indexOfFirst { it.id == id }
        if (index < 0) return MutationResult.NoOp
        if (records.any { it.id != id && it.date == date }) {
            return MutationResult.AlreadyExists
        }
        val old = records[index]
        records[index] = old.copy(date = date, port = port, isBorderPass = isBorderPass)
        persist(records)
        return MutationResult.Success
    }

    fun delete(id: Long): MutationResult {
        val records = loadAll().toMutableList()
        val removed = records.removeAll { it.id == id }
        if (!removed) return MutationResult.NoOp
        persist(records)
        return MutationResult.Success
    }

    /** Delete most recent 刷关 row (by date, then createdAt). */
    fun deleteLatestBorderPass(): MutationResult {
        val records = loadAll()
        val target = records
            .filter { it.isBorderPass }
            .maxWithOrNull(compareBy<HistoryRecord> { it.date }.thenBy { it.createdAt })
            ?: return MutationResult.NoOp
        return delete(target.id)
    }

    /** Delete most recent non-刷关 row (by date, then createdAt). */
    fun deleteLatestNonBorderPass(): MutationResult {
        val records = loadAll()
        val target = records
            .filter { !it.isBorderPass }
            .maxWithOrNull(compareBy<HistoryRecord> { it.date }.thenBy { it.createdAt })
            ?: return MutationResult.NoOp
        return delete(target.id)
    }

    private fun persist(records: List<HistoryRecord>) {
        val array = JSONArray()
        records.forEach { r ->
            array.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("date", r.date)
                    put("port", r.port)
                    put("isBorderPass", r.isBorderPass)
                    put("createdAt", r.createdAt)
                }
            )
        }
        prefs.edit()
            .putString(KEY_HISTORY, array.toString())
            .putLong(KEY_NEXT_ID, idSeq.get())
            .apply()
    }

    private fun parse(raw: String): List<HistoryRecord> {
        val array = JSONArray(raw)
        val list = ArrayList<HistoryRecord>(array.length())
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                HistoryRecord(
                    id = obj.getLong("id"),
                    date = obj.getString("date"),
                    port = obj.getString("port"),
                    isBorderPass = obj.getBoolean("isBorderPass"),
                    createdAt = obj.optLong("createdAt", 0L),
                )
            )
        }
        return list
    }

    companion object {
        private const val PREFS_NAME = "commute_days"
        private const val KEY_HISTORY = "history"
        private const val KEY_NEXT_ID = "next_id"
        val DATE_FMT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun isValidSelectableDate(date: String): Boolean {
            return runCatching {
                val d = LocalDate.parse(date, DATE_FMT)
                !d.isAfter(LocalDate.now())
            }.getOrDefault(false)
        }

        fun todayString(): String = LocalDate.now().format(DATE_FMT)
    }
}
