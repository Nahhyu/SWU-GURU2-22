package com.example.hobbymate.ui.review

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
class WeeklyReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyReviewUiState())
    val uiState: StateFlow<WeeklyReviewUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var currentRequest: Triple<String, Int, Int>? = null

    fun observeWeek(
        hobbyId: String,
        weekNumber: Int,
        targetSessions: Int,
    ) {
        val request = Triple(hobbyId, weekNumber, targetSessions)
        if (currentRequest == request) return
        currentRequest = request
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            reviewRepository
                .observeDailyReviews(hobbyId, weekNumber)
                .collect { reviews ->
                    _uiState.value = WeeklyReviewUiState(
                        summary = WeeklyReviewCalculator.calculate(
                            reviews = reviews,
                            targetSessions = targetSessions,
                        ),
                        isLoading = false,
                    )
                }
        }
    }
}
