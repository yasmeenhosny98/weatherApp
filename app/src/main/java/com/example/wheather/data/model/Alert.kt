package com.example.wheather.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts_table")
data class Alert(
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long,
    @ColumnInfo(name = "event") val event: String,
    @ColumnInfo(name = "requestCode") val requestCode: Int
) {
    @PrimaryKey(autoGenerate = true)
    var alarm_id: Int = 0
}
