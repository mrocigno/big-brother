package br.com.mrocigno.bigbrother.common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tblProxyAction")
class ProxyActionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "proxy_id") val proxyId: Long = 0,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "value") val value: String? = null,
    @ColumnInfo(name = "body") val body: String? = null
)