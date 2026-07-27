package com.example.hobbymate.ui.guide

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbymate.data.repository.VideoAnalysisRepository
import com.example.hobbymate.logic.ChecklistProgression
import com.example.hobbymate.model.VideoAnalysisRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class DailyGuideViewModel @Inject constructor(
    private val repository: VideoAnalysisRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyGuideUiState())
    val uiState: StateFlow<DailyGuideUiState> = _uiState.asStateFlow()

    private var lastRequest: VideoAnalysisRequest? = null
    private var sessionStartedAt = SystemClock.elapsedRealtime()

    fun analyze(request: VideoAnalysisRequest, force: Boolean = false) {
        if (!force && lastRequest == request && (
                _uiState.value.isAnalyzing || _uiState.value.checklist != null
            )
        ) {
            return
        }
        lastRequest = request
        sessionStartedAt = SystemClock.elapsedRealtime()
        _uiState.value = DailyGuideUiState(isAnalyzing = true)
        viewModelScope.launch {
            runCatching { repository.analyze(request) }
                .onSuccess { checklist ->
                    _uiState.value = DailyGuideUiState(checklist = checklist)
                }
                .onFailure { error ->
                    _uiState.value = DailyGuideUiState(
                        errorMessage = analysisErrorMessage(error),
                    )
                }
        }
    }

    fun completeStep(index: Int) {
        val state = _uiState.value
        val totalCount = state.checklist?.steps?.size ?: return
        _uiState.value = state.copy(
            completedStepCount = ChecklistProgression.complete(
                completedCount = state.completedStepCount,
                clickedIndex = index,
                totalCount = totalCount,
            ),
        )
    }

    fun actualMinutes(): Int {
        val elapsedMillis = SystemClock.elapsedRealtime() - sessionStartedAt
        return ceil(elapsedMillis / 60_000.0).toInt().coerceAtLeast(1)
    }

    private fun analysisErrorMessage(error: Throwable): String = when (error) {
        is ConnectException, is UnknownHostException ->
            "OpenAI 분석 서버에 연결할 수 없어요."
        is SocketTimeoutException ->
            "영상 분석 시간이 초과됐어요. 다시 시도해 주세요."
        is HttpException -> when (error.code()) {
            401 -> "OpenAI API 키를 확인해 주세요."
            429 -> "OpenAI 사용량 한도에 도달했어요. 잠시 후 다시 시도해 주세요."
            503 -> "분석 서버에 OPENAI_API_KEY를 설정해 주세요."
            else -> "영상 분석에 실패했어요. (${error.code()})"
        }
        else -> error.message ?: "영상 분석에 실패했어요."
    }
}
