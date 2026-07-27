package com.example.hobbymate.ui.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbymate.data.repository.GuideRepository
import com.example.hobbymate.data.repository.MissingYouTubeApiKeyException
import com.example.hobbymate.data.repository.VideoRepository
import com.example.hobbymate.model.GuideRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class GuideViewModel @Inject constructor(
    private val guideRepository: GuideRepository,
    private val videoRepository: VideoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuideUiState())
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

    fun generateGuide(request: GuideRequest) {
        _uiState.value = _uiState.value.copy(request = request, isLoading = true)
        viewModelScope.launch {
            runCatching { guideRepository.generateGuide(request) }
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false) }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = it.message,
                    )
                }
        }
    }

    fun completeSession(sessionId: Long) {
        viewModelScope.launch { guideRepository.completeSession(sessionId) }
    }

    fun searchVideosForHobby(hobbyId: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            recommendedVideos = emptyList(),
            errorMessage = null,
        )
        viewModelScope.launch {
            runCatching { videoRepository.searchByHobby(hobbyId) }
                .onSuccess { videos ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        recommendedVideos = videos,
                        errorMessage = null,
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        recommendedVideos = emptyList(),
                        errorMessage = videoErrorMessage(error),
                    )
                }
        }
    }

    private fun videoErrorMessage(error: Throwable): String = when (error) {
        is MissingYouTubeApiKeyException -> error.message.orEmpty()
        is UnknownHostException -> "네트워크 연결을 확인한 뒤 다시 시도해 주세요."
        is HttpException -> when (error.code()) {
            400 -> "YouTube 검색 요청을 확인해 주세요."
            403 -> "YouTube API 키, 사용 제한 또는 할당량을 확인해 주세요."
            else -> "YouTube 영상을 불러오지 못했어요. (${error.code()})"
        }
        else -> "YouTube 영상을 불러오지 못했어요. 잠시 후 다시 시도해 주세요."
    }
}
