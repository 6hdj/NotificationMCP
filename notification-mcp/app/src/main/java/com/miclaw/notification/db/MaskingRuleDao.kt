package com.miclaw.notification.db

import androidx.room.*
import com.miclaw.notification.model.MaskingRule

/**
 * 脱敏规则 DAO
 */
@Dao
interface MaskingRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: MaskingRule)

    @Update
    suspend fun update(rule: MaskingRule)

    @Query("DELETE FROM masking_rules WHERE id = :ruleId")
    suspend fun deleteById(ruleId: String)

    @Query("SELECT * FROM masking_rules WHERE id = :ruleId")
    suspend fun getById(ruleId: String): MaskingRule?

    @Query("SELECT * FROM masking_rules WHERE enabled = 1 ORDER BY id")
    suspend fun getEnabled(): List<MaskingRule>

    @Query("SELECT * FROM masking_rules ORDER BY id")
    suspend fun getAll(): List<MaskingRule>

    @Query("UPDATE masking_rules SET enabled = :enabled WHERE id = :ruleId")
    suspend fun setEnabled(ruleId: String, enabled: Boolean)
}
