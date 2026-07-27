package com.example.hobbymate.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.hobbymate.model.PreferenceAnswer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectAnswer(answer: PreferenceAnswer) {
        val answers = _uiState.value.answers
            .filterNot { it.questionId == answer.questionId } + answer
        _uiState.value = _uiState.value.copy(answers = answers)
    }

    fun moveToNextQuestion() {
        val nextIndex = _uiState.value.currentQuestionIndex + 1
        _uiState.value = _uiState.value.copy(
            currentQuestionIndex = nextIndex,
            isComplete = nextIndex >= _uiState.value.questions.size,
        )
    }
}
