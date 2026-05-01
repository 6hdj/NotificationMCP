package com.notificationmcp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notificationmcp.data.db.dao.AutomationRuleDao
import com.notificationmcp.data.db.entity.AutomationRuleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AutomationUiState(
    val rules: List<AutomationRuleEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AutomationViewModel @Inject constructor(
    private val ruleDao: AutomationRuleDao
) : ViewModel() {

    val state: StateFlow<AutomationUiState> = combine(
        ruleDao.getAllFlow()
    ) { rulesArray ->
        AutomationUiState(
            rules = rulesArray[0],
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AutomationUiState()
    )

    fun toggleRule(rule: AutomationRuleEntity) {
        viewModelScope.launch {
            ruleDao.update(rule.copy(isEnabled = !rule.isEnabled))
        }
    }

    fun deleteRule(id: Long) {
        viewModelScope.launch {
            ruleDao.deleteById(id)
        }
    }

    fun addRuleFromEntity(rule: AutomationRuleEntity) {
        viewModelScope.launch {
            ruleDao.insert(rule)
        }
    }
}
