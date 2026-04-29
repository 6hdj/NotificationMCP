package com.miclaw.notification.db

import androidx.room.*
import com.miclaw.notification.model.FilterRule

/**
 * 过滤规则 DAO
 */
@Dao
interface FilterRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: FilterRule)

    @Update
    suspend fun update(rule: FilterRule)

    @Query("DELETE FROM filter_rules WHERE id = :ruleId")
    suspend fun deleteById(ruleId: String)

    @Query("SELECT * FROM filter_rules WHERE id = :ruleId")
    suspend fun getById(ruleId: String): FilterRule?

    @Query("SELECT * FROM filter_rules WHERE enabled = 1 ORDER BY id")
    suspend fun getEnabled(): List<FilterRule>

    @Query("SELECT * FROM filter_rules ORDER BY id")
    suspend fun getAll(): List<FilterRule>

    @Query("UPDATE filter_rules SET enabled = :enabled WHERE id = :ruleId")
    suspend fun setEnabled(ruleId: String, enabled: Boolean)
}
