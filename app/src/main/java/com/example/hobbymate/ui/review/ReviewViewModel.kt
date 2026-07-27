package com.example.hobbymate.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbymate.data.repository.ReviewRepository
import com.example.hobbymate.model.DailyReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun saveDailyReview(review: DailyReview) {
        if (_uiState.value.isSaving) return
        _uiState.value = _uiState.value.copy(
            isSaving = true,
            isSaved = false,
            errorMessage = null,
        )
        viewModelScope.launch {
            runCatching { reviewRepository.saveDailyReview(review) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        dailyReview = review,
                        isSaving = false,
                        isSaved = true,
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = it.message,
                    )
                }
        }
    }

    fun onSaveHandled() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
