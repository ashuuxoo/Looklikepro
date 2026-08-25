package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.TruecallerApiService
import com.example.data.model.PhoneData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PhoneLookupUiState {
    data object Idle : PhoneLookupUiState
    data object Loading : PhoneLookupUiState
    data class Success(
        val searchedNumber: String,
        val data: PhoneData
    ) : PhoneLookupUiState
    data class Error(val message: String) : PhoneLookupUiState
}

class PhoneLookupViewModel(
    private val apiService: TruecallerApiService = TruecallerApiService.create()
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhoneLookupUiState>(PhoneLookupUiState.Idle)
    val uiState: StateFlow<PhoneLookupUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun search(phoneNumber: String = _searchQuery.value) {
        val trimmed = phoneNumber.trim()
        val cleanNumber = trimmed.replace(Regex("[^0-9+]"), "")

        if (cleanNumber.isBlank() || cleanNumber.filter { it.isDigit() }.length < 3) {
            _uiState.value = PhoneLookupUiState.Error("Please enter a valid phone number")
            return
        }

        viewModelScope.launch {
            _uiState.value = PhoneLookupUiState.Loading
            try {
                val response = apiService.lookupPhoneNumber(cleanNumber)
                if (response.success == true && response.data != null && hasDisplayableInfo(response.data)) {
                    _uiState.value = PhoneLookupUiState.Success(
                        searchedNumber = cleanNumber,
                        data = response.data
                    )
                } else {
                    _uiState.value = PhoneLookupUiState.Error("Number not found")
                }
            } catch (e: Exception) {
                _uiState.value = PhoneLookupUiState.Error("Network error. Try again.")
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = PhoneLookupUiState.Idle
    }

    private fun hasDisplayableInfo(data: PhoneData): Boolean {
        return !data.name.isNullOrBlank() ||
                !data.address.isNullOrBlank() ||
                !data.number.isNullOrBlank() ||
                !data.gender.isNullOrBlank() ||
                !data.birthday.isNullOrBlank() ||
                !data.circle.isNullOrBlank() ||
                !data.altMobile.isNullOrBlank()
    }
}
