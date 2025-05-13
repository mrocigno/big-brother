package br.com.mrocigno.bigbrother.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.mrocigno.bigbrother.common.entity.ProxyActionEntity
import br.com.mrocigno.bigbrother.common.entity.ProxyRuleEntity
import br.com.mrocigno.bigbrother.common.entity.ProxyRuleWithActions
import kotlinx.coroutines.flow.Flow

@Dao
interface ProxyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: ProxyRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ProxyActionEntity>)

    @Transaction
    @Query("SELECT * FROM tblProxyRule")
    fun getAll(): Flow<List<ProxyRuleWithActions>>

    @Transaction
    @Query("SELECT * FROM tblProxyRule WHERE id IN (:ids)")
    fun getRules(ids: LongArray): Flow<List<ProxyRuleWithActions>>

    @Transaction
    @Query("SELECT * FROM tblProxyRule WHERE id = :id")
    fun getRuleById(id: Long): ProxyRuleWithActions

    @Transaction
    @Query("SELECT * FROM tblProxyRule WHERE enabled = 1")
    fun getAllEnabled(): List<ProxyRuleWithActions>

    @Query("UPDATE tblProxyRule SET enabled = :enabled WHERE id = :id")
    fun updateEnabledById(id: Long, enabled: Boolean)

    @Query("UPDATE tblProxyRule SET enabled = :enabled")
    fun updateAllEnabled(enabled: Boolean)

    @Query("DELETE FROM tblProxyRule WHERE id = :id")
    suspend fun deleteRuleById(id: Long)

    @Query("DELETE FROM tblProxyAction WHERE id = :id")
    suspend fun deleteActionById(id: Long)

    @Query("DELETE FROM tblProxyAction WHERE proxy_id = :proxyId")
    suspend fun deleteActionsByRuleId(proxyId: Long)
}