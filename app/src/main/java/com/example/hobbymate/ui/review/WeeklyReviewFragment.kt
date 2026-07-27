package com.example.hobbymate.ui.review

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.hobbymate.R
import com.example.hobbymate.data.local.SelectedHobbyStore
import com.example.hobbymate.databinding.FragmentWeeklyReviewBinding
import com.example.hobbymate.model.HobbyProfileCatalog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WeeklyReviewFragment : Fragment() {

    private var _binding: FragmentWeeklyReviewBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel by viewModels<WeeklyReviewViewModel>()

    @Inject
    lateinit var selectedHobbyStore: SelectedHobbyStore

    private val hobbyId: String
        get() = arguments?.getString(ARG_HOBBY_ID)
            .orEmpty()
            .ifBlank { selectedHobbyStore.get() }
    private val weekNumber: Int
        get() = (arguments?.getInt(ARG_WEEK_NUMBER, 1) ?: 1).coerceAtLeast(1)
    private val targetSessions: Int
        get() = (arguments?.getInt(ARG_SESSIONS_PER_WEEK, 5) ?: 5).coerceAtLeast(1)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWeeklyReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hobby = HobbyProfileCatalog.get(hobbyId)
        binding.textWeeklyContext.text = getString(
            R.string.weekly_context_format,
            hobby.displayName,
            weekNumber,
        )
        binding.buttonGoHome.setOnClickListener {
            findNavController().navigate(
                R.id.action_weeklyReviewFragment_to_homeFragment,
            )
        }

        observeReview()
        viewModel.observeWeek(
            hobbyId = hobby.id,
            weekNumber = weekNumber,
            targetSessions = targetSessions,
        )
    }

    private fun observeReview() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state.summary)
                }
            }
        }
    }

    private fun render(summary: WeeklyReviewSummary) {
        binding.textStudiedDays.text = getString(
            R.string.weekly_studied_days_format,
            summary.targetSessions,
            summary.studiedDays,
        )
        binding.textTotalMinutes.text = getString(
            R.string.weekly_time_format,
            summary.totalMinutes,
            summary.averageMinutes,
        )
        binding.textWeeklyAchievement.text = getString(
            R.string.weekly_achievement_format,
            summary.achievementPercent,
        )
        binding.weeklyAchievementProgress.setProgressCompat(
            summary.achievementPercent,
            true,
        )

        binding.textFirstPhotoDay.text = getString(
            R.string.photo_day_format,
            summary.firstPhotoDay,
        )
        binding.textLatestPhotoDay.text = if (summary.latestPhotoUri == null) {
            getString(R.string.latest_photo)
        } else {
            getString(R.string.photo_day_format, summary.latestPhotoDay)
        }
        renderPhoto(
            uri = summary.firstPhotoUri,
            image = binding.imageFirstResult,
            placeholder = binding.textFirstPhotoPlaceholder,
        )
        renderPhoto(
            uri = summary.latestPhotoUri,
            image = binding.imageLatestResult,
            placeholder = binding.textLatestPhotoPlaceholder,
        )

        binding.textGrowthMessage.text = when {
            summary.firstPhotoUri != null && summary.latestPhotoUri != null ->
                getString(
                    R.string.growth_compare_format,
                    (summary.latestPhotoDay - summary.firstPhotoDay + 1)
                        .coerceAtLeast(1),
                )
            summary.firstPhotoUri != null -> getString(R.string.first_photo_saved)
            else -> getString(R.string.photo_comparison_empty)
        }

        val hobby = HobbyProfileCatalog.get(hobbyId)
        val nextTheme = summary.latestStage.ifBlank { hobby.sampleGoal }
        binding.textNextWeekTheme.text = getString(
            R.string.next_week_theme_format,
            weekNumber + 1,
            nextTheme,
        )
    }

    private fun renderPhoto(
        uri: String?,
        image: ImageView,
        placeholder: TextView,
    ) {
        image.isVisible = uri != null
        placeholder.isVisible = uri == null
        if (uri == null) {
            Glide.with(this).clear(image)
            image.setImageDrawable(null)
        } else {
            Glide.with(this)
                .load(Uri.parse(uri))
                .centerCrop()
                .into(image)
        }
    }

    override fun onDestroyView() {
        if (_binding != null) {
            Glide.with(this).clear(binding.imageFirstResult)
            Glide.with(this).clear(binding.imageLatestResult)
        }
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ARG_HOBBY_ID = "hobbyId"
        const val ARG_WEEK_NUMBER = "weekNumber"
        const val ARG_SESSIONS_PER_WEEK = "sessionsPerWeek"
    }
}
