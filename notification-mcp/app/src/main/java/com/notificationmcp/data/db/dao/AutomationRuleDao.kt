package com.notificationmcp.data.db.dao

import androidx.room.*
import com.notificationmcp.data.db.entity.AutomationRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: AutomationRuleEntity): Long

    @Update
    suspend fun update(rule: AutomationRuleEntity)

    @Query("DELETE FROM automation_rules WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM automation_rules ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<AutomationRuleEntity>>

    @Query("SELECT * FROM automation_rules WHERE is_enabled = 1")
    suspend fun getEnabledRules(): List<AutomationRuleEntity>

    @Query("SELECT * FROM automation_rules ORDER BY created_at DESC")
    suspend fun getAll(): List<AutomationRuleEntity>

    @Query("SELECT * FROM automation_rules WHERE id = :id")
    suspend fun getById(id: Long): AutomationRuleEntity?

    @Query("UPDATE automation_rules SET match_count = match_count + 1 WHERE id = :id")
    suspend fun incrementMatchCount(id: Long)
}
