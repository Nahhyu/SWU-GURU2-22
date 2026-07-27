package com.example.hobbymate.ui.guide

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.data.local.ActiveGuideStore
import com.example.hobbymate.databinding.FragmentRoadmapBinding
import com.example.hobbymate.databinding.ItemRoadmapWeekBinding
import com.example.hobbymate.logic.GuideProgressCalculator
import com.example.hobbymate.logic.RoadmapCurriculum
import com.example.hobbymate.model.ActiveGuideConfig
import com.example.hobbymate.model.HobbyProfileCatalog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RoadmapFragment : Fragment() {

    private var _binding: FragmentRoadmapBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel by viewModels<RoadmapViewModel>()

    @Inject
    lateinit var activeGuideStore: ActiveGuideStore

    private lateinit var guide: ActiveGuideConfig
    private var currentWeek = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRoadmapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        guide = guideFromArguments()
        binding.textRoadmapGoal.text = guide.goal
        binding.textRoadmapTags.text = getString(
            R.string.roadmap_tags_format,
            guide.levelLabel,
            guide.durationWeeks,
            guide.minutesPerSession,
            guide.sessionsPerWeek,
        )
        binding.buttonOpenWeek.setOnClickListener { openCurrentWeek() }
        observeProgress()
        viewModel.observe(guide.hobbyId)
    }

    private fun observeProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val progress = GuideProgressCalculator.calculate(
                        reviews = state.reviews,
                        durationWeeks = guide.durationWeeks,
                        sessionsPerWeek = guide.sessionsPerWeek,
                    )
                    currentWeek = progress.currentWeek
                    binding.textRoadmapProgress.text = getString(
                        R.string.roadmap_progress_format,
                        progress.completedWeeks,
                        guide.durationWeeks,
                    )
                    binding.roadmapProgress.setProgressCompat(
                        progress.overallPercent,
                        true,
                    )
                    binding.buttonOpenWeek.text = getString(
                        R.string.view_dynamic_week_sessions,
                        currentWeek,
                    )
                    renderWeeks(state.reviews)
                }
            }
        }
    }

    private fun renderWeeks(reviews: List<com.example.hobbymate.model.DailyReview>) {
        val hobby = HobbyProfileCatalog.get(guide.hobbyId)
        val completedByWeek = reviews
            .filter { it.totalSteps > 0 && it.completedSteps >= it.totalSteps }
            .groupingBy { it.weekNumber }
            .eachCount()
        binding.weekContainer.removeAllViews()

        (1..guide.durationWeeks).forEach { weekNumber ->
            val completedSessions = completedByWeek[weekNumber] ?: 0
            val isCompleted = completedSessions >= guide.sessionsPerWeek
            val isCurrent = weekNumber == currentWeek
            val theme = RoadmapCurriculum.theme(
                hobby = hobby,
                goal = guide.goal,
                weekNumber = weekNumber,
                totalWeeks = guide.durationWeeks,
            )
            val item = ItemRoadmapWeekBinding.inflate(
                layoutInflater,
                binding.weekContainer,
                false,
            )
            item.textWeekBadge.text = if (isCompleted) "✓" else weekNumber.toString()
            item.textWeekTitle.text = getString(
                R.string.roadmap_week_title_format,
                weekNumber,
                theme,
            )
            item.textWeekStatus.text = getString(
                when {
                    isCompleted -> R.string.completed
                    isCurrent -> R.string.in_progress
                    else -> R.string.scheduled
                },
            )
            item.weekCard.setBackgroundResource(
                when {
                    isCompleted -> R.drawable.bg_card_success
                    isCurrent -> R.drawable.bg_card_selected
                    else -> R.drawable.bg_card
                },
            )
            item.textWeekBadge.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    when {
                        isCompleted -> R.color.success
                        isCurrent -> R.color.primary
                        else -> R.color.surface
                    },
                ),
            )
            item.textWeekSessions.isVisible = isCurrent
            if (isCurrent) {
                item.textWeekSessions.text = (1..guide.sessionsPerWeek)
                    .joinToString("\n") { day ->
                        val marker = if (day <= completedSessions) "●" else "○"
                        "$marker  Day $day  ${
                            RoadmapCurriculum.sessionTitle(theme, day)
                        }"
                    }
                item.weekCard.setOnClickListener { openCurrentWeek() }
            }
            binding.weekContainer.addView(item.root)
        }
    }

    private fun openCurrentWeek() {
        findNavController().navigate(
            R.id.action_roadmapFragment_to_sessionListFragment,
            bundleOf(
                ARG_HOBBY_ID to guide.hobbyId,
                ARG_HOBBY_GOAL to guide.goal,
                ARG_WEEK_NUMBER to currentWeek,
                ARG_SESSIONS_PER_WEEK to guide.sessionsPerWeek,
            ),
        )
    }

    private fun guideFromArguments(): ActiveGuideConfig {
        val stored = activeGuideStore.get()
        val hobbyId = arguments?.getString(ARG_HOBBY_ID)
            .orEmpty()
            .ifBlank { stored?.hobbyId ?: DEFAULT_HOBBY_ID }
        return ActiveGuideConfig(
            hobbyId = hobbyId,
            level = arguments?.getString(ARG_LEVEL)
                .orEmpty()
                .ifBlank { stored?.level ?: "first" },
            levelLabel = arguments?.getString(ARG_LEVEL_LABEL)
                .orEmpty()
                .ifBlank { stored?.levelLabel ?: getString(R.string.level_first) },
            goal = arguments?.getString(ARG_GOAL)
                .orEmpty()
                .ifBlank {
                    stored?.goal ?: HobbyProfileCatalog.get(hobbyId).sampleGoal
                },
            durationWeeks = (
                arguments?.getInt(ARG_DURATION_WEEKS, stored?.durationWeeks ?: 12)
                    ?: stored?.durationWeeks
                    ?: 12
                ).coerceAtLeast(1),
            minutesPerSession = (
                arguments?.getInt(ARG_MINUTES_PER_SESSION, stored?.minutesPerSession ?: 30)
                    ?: stored?.minutesPerSession
                    ?: 30
                ).coerceAtLeast(1),
            sessionsPerWeek = (
                arguments?.getInt(ARG_SESSIONS_PER_WEEK, stored?.sessionsPerWeek ?: 5)
                    ?: stored?.sessionsPerWeek
                    ?: 5
                ).coerceAtLeast(1),
            createdAt = stored?.createdAt ?: System.currentTimeMillis(),
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ARG_HOBBY_ID = "hobbyId"
        const val ARG_LEVEL = "level"
        const val ARG_LEVEL_LABEL = "levelLabel"
        const val ARG_GOAL = "goal"
        const val ARG_HOBBY_GOAL = "hobbyGoal"
        const val ARG_DURATION_WEEKS = "durationWeeks"
        const val ARG_MINUTES_PER_SESSION = "minutesPerSession"
        const val ARG_SESSIONS_PER_WEEK = "sessionsPerWeek"
        const val ARG_WEEK_NUMBER = "weekNumber"
        const val DEFAULT_HOBBY_ID = "climbing"
    }
}
