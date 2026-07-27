package com.example.hobbymate.ui.hobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbymate.data.repository.HobbyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HobbyViewModel @Inject constructor(
    private val hobbyRepository: HobbyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HobbyUiState())
    val uiState: StateFlow<HobbyUiState> = _uiState.asStateFlow()

    fun loadHobbies() {
        viewModelScope.launch {
            hobbyRepository.observeHobbies().collect { hobbies ->
                _uiState.value = _uiState.value.copy(
                    allHobbies = hobbies,
                    recommendations = hobbies.take(5),
                )
            }
        }
    }

    fun selectHobby(hobbyId: String) {
        _uiState.value = _uiState.value.copy(
            selectedHobby = _uiState.value.allHobbies.firstOrNull { it.id == hobbyId },
        )
    }
}
