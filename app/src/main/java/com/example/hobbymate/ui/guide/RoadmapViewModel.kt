package com.example.hobbymate.ui.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbymate.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()
    private var observeJob: Job? = null
    private var observedHobbyId: String? = null

    fun observe(hobbyId: String) {
        if (observedHobbyId == hobbyId) return
        observedHobbyId = hobbyId
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            reviewRepository.observeAllDailyReviews(hobbyId).collect { reviews ->
                _uiState.value = RoadmapUiState(
                    reviews = reviews,
                    isLoading = false,
                )
            }
        }
    }
}
