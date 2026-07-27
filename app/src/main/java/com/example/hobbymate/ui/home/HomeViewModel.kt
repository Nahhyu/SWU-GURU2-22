package com.example.hobbymate.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbymate.data.local.ActiveGuideStore
import com.example.hobbymate.data.local.SelectedHobbyStore
import com.example.hobbymate.data.repository.ReviewRepository
import com.example.hobbymate.model.HobbyProfileCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    selectedHobbyStore: SelectedHobbyStore,
    activeGuideStore: ActiveGuideStore,
    reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val activeReviews = activeGuideStore.activeGuide.flatMapLatest { guide ->
            if (guide == null) {
                flowOf(emptyList())
            } else {
                reviewRepository.observeAllDailyReviews(guide.hobbyId)
            }
        }
        viewModelScope.launch {
            combine(
                selectedHobbyStore.selectedHobbyIds,
                activeGuideStore.activeGuide,
                activeReviews,
            ) { selectedIds, activeGuide, reviews ->
                val selectedProfiles = selectedIds.map(HobbyProfileCatalog::get)
                HomeUiState(
                    activeGuide = activeGuide,
                    activeReviews = reviews,
                    selectedHobbies = selectedProfiles,
                    recommendations = HobbyProfileCatalog.all()
                        .filterNot { it.id in selectedIds }
                        .take(RECOMMENDATION_COUNT),
                    isLoading = false,
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    private companion object {
        const val RECOMMENDATION_COUNT = 4
    }
}
