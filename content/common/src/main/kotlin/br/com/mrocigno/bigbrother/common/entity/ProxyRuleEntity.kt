package br.com.mrocigno.bigbrother.common.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "tblProxyRule")
class ProxyRuleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "rule_name") val ruleName: String,
    @ColumnInfo(name = "path_condition") val pathCondition: String,
    @ColumnInfo(name = "header_condition") val headerCondition: String,
    @ColumnInfo(name = "enabled") val enabled: Boolean = true
)

data class ProxyRuleWithActions(
    @Embedded
    val rule: ProxyRuleEntity,
    @Relation(parentColumn = "id", entityColumn = "proxy_id")
    val actions: List<ProxyActionEntity>
)