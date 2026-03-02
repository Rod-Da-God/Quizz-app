package com.example.kurs.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kurs.domain.usecase.auth.RegisterUseCase
import com.example.kurs.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>?>(null)
    val uiState: StateFlow<UiState<Unit>?> = _uiState

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = registerUseCase(username, email, password)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Ошибка") }
            )
        }
    }
}