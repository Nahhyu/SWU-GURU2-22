package com.example.hobbymate.ui.hobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.databinding.FragmentHobbyRecommendBinding
import com.example.hobbymate.logic.HobbyProfileRecommendationCalculator
import com.example.hobbymate.model.HobbyProfile
import com.example.hobbymate.model.HobbyProfileCatalog
import com.example.hobbymate.model.UserPreference
import com.example.hobbymate.ui.common.renderHobbyIcon
import com.google.android.material.card.MaterialCardView

class HobbyRecommendFragment : Fragment() {

    private var _binding: FragmentHobbyRecommendBinding? = null
    private val binding get() = checkNotNull(_binding)
    private var selectedIndex = 0
    private var recommendations: List<Pair<HobbyProfile, Int>> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHobbyRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedIndex = savedInstanceState?.getInt(KEY_SELECTED_INDEX) ?: 0
        recommendations = HobbyProfileRecommendationCalculator.recommend(
            UserPreference(
                environment = argumentScore(ARG_ENVIRONMENT),
                social = argumentScore(ARG_SOCIAL),
                physicalIntensity = argumentScore(ARG_PHYSICAL),
                budget = argumentScore(ARG_BUDGET),
                structureCreativity = argumentScore(ARG_CREATIVITY),
            ),
        )
        renderRecommendations()
        binding.buttonRecommendationDetail.setOnClickListener {
            val selected = recommendations.getOrNull(selectedIndex)?.first
                ?: return@setOnClickListener
            findNavController().navigate(
                R.id.action_hobbyRecommendFragment_to_hobbyInfoFragment,
                bundleOf(ARG_HOBBY_ID to selected.id),
            )
        }
        binding.buttonBrowseAll.setOnClickListener {
            findNavController().navigate(
                R.id.action_hobbyRecommendFragment_to_hobbySelectFragment,
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_INDEX, selectedIndex)
        super.onSaveInstanceState(outState)
    }

    private fun renderRecommendations() {
        binding.recommendationContainer.removeAllViews()
        recommendations.forEachIndexed { index, recommendation ->
            val profile = recommendation.first
            val match = recommendation.second
            val card = layoutInflater.inflate(
                R.layout.item_hobby_recommendation,
                binding.recommendationContainer,
                false,
            ) as MaterialCardView

            card.findViewById<TextView>(R.id.hobbyIcon).renderHobbyIcon(profile)
            card.findViewById<TextView>(R.id.hobbyName).text = profile.displayName
            card.findViewById<TextView>(R.id.hobbyCategory).text =
                HobbyProfileCatalog.categoryLabel(profile.id)
            card.findViewById<TextView>(R.id.hobbyMatch).text =
                getString(R.string.match_percent, match)
            card.findViewById<ProgressBar>(R.id.hobbyProgress).progress = match
            card.findViewById<RadioButton>(R.id.hobbyRadio).isChecked =
                index == selectedIndex
            card.strokeColor = ContextCompat.getColor(
                requireContext(),
                if (index == selectedIndex) R.color.primary else R.color.outline,
            )
            card.strokeWidth = dp(if (index == selectedIndex) 2 else 1)
            card.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (index == selectedIndex) {
                        R.color.primary_container
                    } else {
                        R.color.surface
                    },
                ),
            )
            card.setOnClickListener {
                selectedIndex = index
                renderRecommendations()
            }
            binding.recommendationContainer.addView(card)
        }
        binding.textSelectedCount.text = getString(
            R.string.selected_count,
            if (recommendations.isEmpty()) 0 else 1,
        )
        binding.buttonRecommendationDetail.isEnabled = recommendations.isNotEmpty()
    }

    private fun argumentScore(key: String): Double =
        arguments?.getDouble(key, DEFAULT_SCORE) ?: DEFAULT_SCORE

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val KEY_SELECTED_INDEX = "selected_index"
        const val ARG_HOBBY_ID = "hobbyId"
        const val ARG_ENVIRONMENT = "preferenceEnvironment"
        const val ARG_SOCIAL = "preferenceSocial"
        const val ARG_PHYSICAL = "preferencePhysical"
        const val ARG_BUDGET = "preferenceBudget"
        const val ARG_CREATIVITY = "preferenceCreativity"
        const val DEFAULT_SCORE = 0.5
    }
}
