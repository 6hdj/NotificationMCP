package com.notificationmcp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.notificationmcp.data.db.entity.NotificationEntity
import com.notificationmcp.data.repository.NotificationRepository
import com.notificationmcp.privacy.PrivacyEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryState(
    val notifications: List<NotificationEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val selectedNotification: NotificationEntity? = null,
    val groupedByApp: Boolean = false
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val repository: NotificationRepository,
    private val privacyEngine: PrivacyEngine
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            repository.getRecentNotificationsFlow(200).collect { list ->
                _state.value = _state.value.copy(notifications = list)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        if (query.isBlank()) {
            loadNotifications()
        } else {
            viewModelScope.launch {
                val results = repository.searchNotifications(contentKeyword = query, limit = 200)
                _state.value = _state.value.copy(notifications = results)
            }
        }
    }

    fun selectNotification(notification: NotificationEntity?) {
        _state.value = _state.value.copy(selectedNotification = notification)
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.softDelete(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.softDeleteAll()
        }
    }

    fun toggleGroupByApp() {
        _state.value = _state.value.copy(groupedByApp = !_state.value.groupedByApp)
    }
}
