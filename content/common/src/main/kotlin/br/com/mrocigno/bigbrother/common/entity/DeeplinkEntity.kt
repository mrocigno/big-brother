package br.com.mrocigno.bigbrother.common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "tblDeeplink")
data class DeeplinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "activity_name") val activityName: String,
    @ColumnInfo(name = "exported") val exported: Boolean,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "actions") val actions: List<String>,
    @ColumnInfo(name = "categories") val categories: List<String>,
    @ColumnInfo(name = "time") val time: LocalDateTime = LocalDateTime.now()
)
