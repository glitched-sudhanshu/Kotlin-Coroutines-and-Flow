package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase8

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch

class RoomAndCoroutinesViewModel(
    private val api: MockApi,
    private val database: AndroidVersionDao,
) : BaseViewModel<UiState>() {
    fun loadData() {
        uiState.value = UiState.Loading.LoadFromDb
        viewModelScope.launch {
            val dbList = database.getAndroidVersions()
            if (dbList.isNotEmpty()) {
                uiState.value = UiState.Success(DataSource.DATABASE, dbList.mapToUiModelList())
            } else {
                uiState.value = UiState.Error(DataSource.DATABASE, "Db is empty!")
            }

            uiState.value = UiState.Loading.LoadFromNetwork
            try {
                val recentList = api.getRecentAndroidVersions()
                for (version in recentList) {
                    database.insert(version.mapToEntity())
                }
                uiState.value = UiState.Success(DataSource.NETWORK, recentList)
            } catch (_: Exception) {
                uiState.value = UiState.Error(DataSource.NETWORK, "Something went wrong!")
            }
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            database.clear()
        }
    }
}

enum class DataSource(val dataSourceName: String) {
    DATABASE("Database"),
    NETWORK("Network"),
}
