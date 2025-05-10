package br.com.mrocigno.bigbrother.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.mrocigno.bigbrother.common.entity.ProxyActionEntity
import br.com.mrocigno.bigbrother.common.entity.ProxyRuleEntity
import br.com.mrocigno.bigbrother.common.entity.ProxyRuleWithActions

@Dao
interface ProxyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: ProxyRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ProxyActionEntity>)

    @Transaction
    @Query("SELECT * FROM tblProxyRule")
    suspend fun getAll(): List<ProxyRuleWithActions>

    @Transaction
    @Query("SELECT * FROM tblProxyRule WHERE enabled = 1")
    fun getAllEnabled(): List<ProxyRuleWithActions>
}