package com.example.hobbymate.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.data.local.ActiveGuideStore
import com.example.hobbymate.data.local.SelectedHobbyStore
import com.example.hobbymate.databinding.FragmentGuideSetupBinding
import com.example.hobbymate.model.HobbyProfileCatalog
import com.example.hobbymate.model.GuideRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GuideSetupFragment : Fragment() {

    private var _binding: FragmentGuideSetupBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var activeGuideStore: ActiveGuideStore

    @Inject
    lateinit var selectedHobbyStore: SelectedHobbyStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGuideSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.requestFocus()
        if (savedInstanceState == null) {
            val hobbyId = arguments?.getString(ARG_HOBBY_ID) ?: DEFAULT_HOBBY_ID
            binding.editGoal.setText(HobbyProfileCatalog.get(hobbyId).sampleGoal)
        }
        binding.editGoal.doAfterTextChanged {
            if (binding.goalInputLayout.error != null) validateInputs()
            updateGenerateButton()
        }
        binding.editDurationWeeks.doAfterTextChanged {
            if (binding.durationInputLayout.error != null) validateInputs()
            updateGenerateButton()
        }
        updateGenerateButton()

        binding.buttonGenerateGuide.setOnClickListener {
            if (!validateInputs(showErrors = true)) return@setOnClickListener

            val request = createGuideRequest()
            val levelLabel = selectedLevelLabel()
            selectedHobbyStore.save(request.hobbyId)
            activeGuideStore.save(request, levelLabel)
            hideKeyboard()
            binding.formContainer.isVisible = false
            binding.loadingContainer.isVisible = true
            binding.textLoadingSubtitle.text = getString(
                R.string.loading_curriculum_format,
                request.durationWeeks,
            )
            viewLifecycleOwner.lifecycleScope.launch {
                delay(LOADING_DURATION_MS)
                if (
                    findNavController().currentDestination?.id ==
                    R.id.guideSetupFragment
                ) {
                    findNavController().navigate(
                        R.id.action_guideSetupFragment_to_roadmapFragment,
                        bundleOf(
                            ARG_HOBBY_ID to request.hobbyId,
                            ARG_LEVEL to request.level,
                            ARG_LEVEL_LABEL to levelLabel,
                            ARG_GOAL to request.goal,
                            ARG_DURATION_WEEKS to request.durationWeeks,
                            ARG_MINUTES_PER_SESSION to request.minutesPerSession,
                            ARG_SESSIONS_PER_WEEK to request.sessionsPerWeek,
                        ),
                    )
                }
            }
        }
    }

    private fun updateGenerateButton() {
        binding.buttonGenerateGuide.isEnabled = validateInputs()
    }

    private fun validateInputs(showErrors: Boolean = false): Boolean {
        val goalValid = binding.editGoal.text?.toString()?.trim().orEmpty().isNotBlank()
        val duration = binding.editDurationWeeks.text?.toString()?.toIntOrNull()
        val durationValid = duration != null && duration in MIN_DURATION_WEEKS..MAX_DURATION_WEEKS

        if (showErrors || binding.goalInputLayout.error != null) {
            binding.goalInputLayout.error =
                if (goalValid) null else getString(R.string.goal_required)
        }
        if (showErrors || binding.durationInputLayout.error != null) {
            binding.durationInputLayout.error =
                if (durationValid) null else getString(R.string.duration_invalid)
        }
        return goalValid && durationValid
    }

    private fun createGuideRequest() = GuideRequest(
        hobbyId = arguments?.getString(ARG_HOBBY_ID) ?: DEFAULT_HOBBY_ID,
        level = when (binding.levelToggle.checkedButtonId) {
            R.id.buttonLevelSome -> LEVEL_SOME
            R.id.buttonLevelRegular -> LEVEL_REGULAR
            R.id.buttonLevelExpert -> LEVEL_EXPERT
            else -> LEVEL_FIRST
        },
        goal = binding.editGoal.text?.toString()?.trim().orEmpty(),
        durationWeeks = checkNotNull(
            binding.editDurationWeeks.text?.toString()?.toIntOrNull(),
        ),
        minutesPerSession = when (binding.timeToggle.checkedButtonId) {
            R.id.buttonTime15 -> 15
            R.id.buttonTime60 -> 60
            else -> 30
        },
        sessionsPerWeek = when (binding.frequencyToggle.checkedButtonId) {
            R.id.buttonFrequencyThree -> 3
            else -> 5
        },
    )

    private fun selectedLevelLabel(): String = getString(
        when (binding.levelToggle.checkedButtonId) {
            R.id.buttonLevelSome -> R.string.level_some
            R.id.buttonLevelRegular -> R.string.level_regular
            R.id.buttonLevelExpert -> R.string.level_expert
            else -> R.string.level_first
        },
    )

    private fun hideKeyboard() {
        ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.root.requestFocus()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val LOADING_DURATION_MS = 1_200L
        const val MIN_DURATION_WEEKS = 1
        const val MAX_DURATION_WEEKS = 52
        const val ARG_HOBBY_ID = "hobbyId"
        const val ARG_LEVEL = "level"
        const val ARG_LEVEL_LABEL = "levelLabel"
        const val ARG_GOAL = "goal"
        const val ARG_DURATION_WEEKS = "durationWeeks"
        const val ARG_MINUTES_PER_SESSION = "minutesPerSession"
        const val ARG_SESSIONS_PER_WEEK = "sessionsPerWeek"
        const val DEFAULT_HOBBY_ID = "climbing"
        const val LEVEL_FIRST = "first"
        const val LEVEL_SOME = "some"
        const val LEVEL_REGULAR = "regular"
        const val LEVEL_EXPERT = "expert"
    }
}
