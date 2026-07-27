package com.example.hobbymate.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Space
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.databinding.FragmentHomeBinding
import com.example.hobbymate.databinding.ItemHomePendingHobbyBinding
import com.example.hobbymate.databinding.ItemHomeProgressHobbyBinding
import com.example.hobbymate.logic.GuideProgressCalculator
import com.example.hobbymate.model.ActiveGuideConfig
import com.example.hobbymate.model.HobbyProfile
import com.example.hobbymate.model.HobbyProfileCatalog
import com.example.hobbymate.ui.common.renderHobbyIcon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonFindNewHobby.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_onboardingFragment,
            )
        }
        observeHome()
    }

    private fun observeHome() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: HomeUiState) {
        val guide = state.activeGuide
        binding.activeGuideCard.isVisible = guide != null
        binding.emptyGuideCard.isVisible = guide == null
        binding.activeGuideCard.setOnClickListener(
            guide?.let { activeGuide ->
                View.OnClickListener { openRoadmap(activeGuide) }
            },
        )

        binding.selectedHobbyContainer.removeAllViews()
        if (guide != null) {
            val profile = HobbyProfileCatalog.get(guide.hobbyId)
            val progress = GuideProgressCalculator.calculate(
                reviews = state.activeReviews,
                durationWeeks = guide.durationWeeks,
                sessionsPerWeek = guide.sessionsPerWeek,
            )
            binding.textHomeActiveHobby.text = getString(
                R.string.home_active_hobby_dynamic_format,
                profile.displayName,
                progress.currentWeek,
            )
            binding.textHomeTodayGoal.text = getString(
                R.string.home_today_goal_format,
                guide.goal,
            )
            binding.homeGuideProgress.setProgressCompat(
                progress.overallPercent,
                true,
            )
            addProgressCard(
                profile = profile,
                guide = guide,
                currentWeek = progress.currentWeek,
                completedThisWeek = progress.completedSessionsThisWeek,
                progressPercent = progress.overallPercent,
            )
            completeGridRow(binding.selectedHobbyContainer)
        }
        val activeHobbyCount = if (guide == null) 0 else 1
        binding.textSelectedHobbiesHeader.isVisible = activeHobbyCount > 0
        binding.selectedHobbyContainer.isVisible = activeHobbyCount > 0
        binding.textSelectedHobbiesHeader.text = getString(
            R.string.home_selected_hobbies_format,
            activeHobbyCount,
        )

        binding.homeRecommendationContainer.removeAllViews()
        val pendingHobbies = (
            state.selectedHobbies.filterNot { it.id == guide?.hobbyId } +
                state.recommendations
            ).distinctBy(HobbyProfile::id)
        binding.textPendingHobbiesHeader.isVisible = pendingHobbies.isNotEmpty()
        binding.homeRecommendationContainer.isVisible = pendingHobbies.isNotEmpty()
        pendingHobbies.forEach { profile ->
            addHobbyCard(profile, binding.homeRecommendationContainer)
        }
        completeGridRow(binding.homeRecommendationContainer)
    }

    private fun addProgressCard(
        profile: HobbyProfile,
        guide: ActiveGuideConfig,
        currentWeek: Int,
        completedThisWeek: Int,
        progressPercent: Int,
    ) {
        val item = ItemHomeProgressHobbyBinding.inflate(
            layoutInflater,
            binding.selectedHobbyContainer,
            false,
        )
        item.textProgressHobbyIcon.renderHobbyIcon(profile)
        item.textProgressHobbyName.text = profile.displayName
        item.textProgressPercent.text = getString(
            R.string.percent_format,
            progressPercent,
        )
        item.textProgressHobbyMeta.text = getString(
            R.string.home_hobby_progress_format,
            currentWeek,
            completedThisWeek,
            guide.sessionsPerWeek,
        )
        item.root.setOnClickListener { openRoadmap(guide) }
        addGridItem(item.root, binding.selectedHobbyContainer)
    }

    private fun addHobbyCard(
        profile: HobbyProfile,
        container: GridLayout,
    ) {
        val item = ItemHomePendingHobbyBinding.inflate(
            layoutInflater,
            container,
            false,
        )
        item.textPendingHobbyIcon.renderHobbyIcon(profile)
        item.textPendingHobbyName.text = profile.displayName
        item.textPendingHobbyCategory.text =
            HobbyProfileCatalog.categoryLabel(profile.id)
        item.root.setOnClickListener { openHobby(profile.id) }
        addGridItem(item.root, container)
    }

    private fun addGridItem(
        view: View,
        container: GridLayout,
    ) {
        val index = container.childCount
        val requestedHeight = view.layoutParams?.height
            ?: ViewGroup.LayoutParams.WRAP_CONTENT
        val column = index % GRID_COLUMN_COUNT
        val row = index / GRID_COLUMN_COUNT
        view.layoutParams = gridLayoutParams(
            row = row,
            column = column,
            height = requestedHeight,
            includeBottomMargin = true,
        )
        container.addView(view)
    }

    private fun completeGridRow(container: GridLayout) {
        if (container.childCount % GRID_COLUMN_COUNT == 0) return
        val index = container.childCount
        container.addView(
            Space(requireContext()).apply {
                layoutParams = gridLayoutParams(
                    row = index / GRID_COLUMN_COUNT,
                    column = index % GRID_COLUMN_COUNT,
                    height = 0,
                    includeBottomMargin = false,
                )
            },
        )
    }

    private fun gridLayoutParams(
        row: Int,
        column: Int,
        height: Int,
        includeBottomMargin: Boolean,
    ): GridLayout.LayoutParams =
        GridLayout.LayoutParams(
            GridLayout.spec(row),
            GridLayout.spec(column, 1, 1f),
        ).apply {
            width = 0
            this.height = height
            setGravity(Gravity.FILL_HORIZONTAL)
            setMargins(
                if (column == 0) 0 else dp(GRID_GUTTER_HALF_DP),
                0,
                if (column == 0) dp(GRID_GUTTER_HALF_DP) else 0,
                if (includeBottomMargin) dp(GRID_ROW_GAP_DP) else 0,
            )
        }

    private fun openHobby(hobbyId: String) {
        findNavController().navigate(
            R.id.action_homeFragment_to_hobbyInfoFragment,
            bundleOf(ARG_HOBBY_ID to hobbyId),
        )
    }

    private fun openRoadmap(guide: ActiveGuideConfig) {
        findNavController().navigate(
            R.id.action_homeFragment_to_roadmapFragment,
            bundleOf(
                ARG_HOBBY_ID to guide.hobbyId,
                ARG_LEVEL to guide.level,
                ARG_LEVEL_LABEL to guide.levelLabel,
                ARG_GOAL to guide.goal,
                ARG_DURATION_WEEKS to guide.durationWeeks,
                ARG_MINUTES_PER_SESSION to guide.minutesPerSession,
                ARG_SESSIONS_PER_WEEK to guide.sessionsPerWeek,
            ),
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val GRID_COLUMN_COUNT = 2
        const val GRID_GUTTER_HALF_DP = 7
        const val GRID_ROW_GAP_DP = 16
        const val ARG_HOBBY_ID = "hobbyId"
        const val ARG_LEVEL = "level"
        const val ARG_LEVEL_LABEL = "levelLabel"
        const val ARG_GOAL = "goal"
        const val ARG_DURATION_WEEKS = "durationWeeks"
        const val ARG_MINUTES_PER_SESSION = "minutesPerSession"
        const val ARG_SESSIONS_PER_WEEK = "sessionsPerWeek"
    }
}
