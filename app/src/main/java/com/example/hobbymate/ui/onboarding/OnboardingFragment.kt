package com.example.hobbymate.ui.onboarding

import android.os.Bundle
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.databinding.FragmentOnboardingBinding
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = checkNotNull(_binding)
    private var currentQuestion = 0
    private var selectedAnswers = IntArray(QUESTION_COUNT) { NEUTRAL_OPTION_INDEX }

    private val questions = listOf(
        Question(
            R.string.question_one,
            listOf(
                "무조건 실내가 좋아요",
                "실내가 조금 더 편해요",
                "상관없어요",
                "야외가 조금 더 좋아요",
                "무조건 밖에서 하고 싶어요",
            ),
        ),
        Question(
            R.string.question_two,
            listOf(
                "완전히 혼자가 좋아요",
                "혼자가 좀 더 편해요",
                "상관없어요",
                "같이 하는 게 좋아요",
                "무조건 사람들이랑 같이요",
            ),
        ),
        Question(
            R.string.question_three,
            listOf(
                "거의 안 움직이고 싶어요",
                "가볍게 움직이는 정도요",
                "적당히 움직이고 싶어요",
                "활발하게 움직이고 싶어요",
                "땀 흠뻑 흘리고 싶어요",
            ),
        ),
        Question(
            R.string.question_four,
            listOf(
                "최소한만 쓰고 싶어요",
                "저렴하면 좋겠어요",
                "상관없어요",
                "어느 정도는 투자할 수 있어요",
                "비용은 신경 안 써요",
            ),
        ),
        Question(
            R.string.question_five,
            listOf(
                "정해진 순서대로 차근차근이요",
                "커리큘럼은 있되 유연하게요",
                "상관없어요",
                "자유롭게 창작 위주로요",
                "완전히 제 마음대로요",
            ),
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentQuestion = savedInstanceState?.getInt(KEY_QUESTION_INDEX) ?: 0
        selectedAnswers = savedInstanceState
            ?.getIntArray(KEY_SELECTED_ANSWERS)
            ?: IntArray(QUESTION_COUNT) { NEUTRAL_OPTION_INDEX }
        renderQuestion()
        binding.buttonSkip.setOnClickListener {
            findNavController().navigate(
                R.id.action_onboardingFragment_to_hobbySelectFragment,
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_QUESTION_INDEX, currentQuestion)
        outState.putIntArray(KEY_SELECTED_ANSWERS, selectedAnswers)
        super.onSaveInstanceState(outState)
    }

    private fun renderQuestion() {
        val question = questions[currentQuestion]
        binding.textQuestion.setText(question.title)
        binding.textStep.text = getString(
            R.string.step_format,
            currentQuestion + 1,
        )
        updateProgress()

        binding.optionsContainer.removeAllViews()
        question.options.forEachIndexed { index, option ->
            binding.optionsContainer.addView(createOptionCard(option, index))
        }
    }

    private fun createOptionCard(option: String, optionIndex: Int): View {
        val card = MaterialCardView(requireContext()).apply {
            radius = dp(18).toFloat()
            strokeWidth = dp(1)
            strokeColor = ContextCompat.getColor(requireContext(), R.color.outline)
            cardElevation = 0f
            setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.surface),
            )
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(64),
            ).apply {
                bottomMargin = dp(11)
            }
            isClickable = true
            isFocusable = true
        }

        val row = LinearLayout(requireContext()).apply {
            gravity = android.view.Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(21), 0, dp(18), 0)
        }
        val label = TextView(requireContext()).apply {
            text = option
            setTextColor(ContextCompat.getColor(requireContext(), R.color.on_background))
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        val arrow = TextView(requireContext()).apply {
            text = "›"
            setTextColor(
                ContextCompat.getColor(requireContext(), R.color.on_surface_variant),
            )
            textSize = 22f
        }
        row.addView(label)
        row.addView(arrow)
        card.addView(row)
        card.setOnClickListener { moveNext(optionIndex) }
        return card
    }

    private fun moveNext(optionIndex: Int) {
        selectedAnswers[currentQuestion] = optionIndex
        if (currentQuestion == questions.lastIndex) {
            findNavController().navigate(
                R.id.action_onboardingFragment_to_hobbyRecommendFragment,
                bundleOf(
                    ARG_ENVIRONMENT to normalizedAnswer(0),
                    ARG_SOCIAL to normalizedAnswer(1),
                    ARG_PHYSICAL to normalizedAnswer(2),
                    ARG_BUDGET to normalizedAnswer(3),
                    ARG_CREATIVITY to normalizedAnswer(4),
                ),
            )
        } else {
            currentQuestion += 1
            renderQuestion()
        }
    }

    private fun updateProgress() {
        val segments = listOf(
            binding.progressOne,
            binding.progressTwo,
            binding.progressThree,
            binding.progressFour,
            binding.progressFive,
        )
        segments.forEachIndexed { index, segment ->
            segment.background = ContextCompat.getDrawable(
                requireContext(),
                if (index <= currentQuestion) {
                    R.drawable.bg_progress_active
                } else {
                    R.drawable.bg_progress_track
                },
            )
            segment.layoutParams = (segment.layoutParams as LinearLayout.LayoutParams).apply {
                width = dp(if (index <= currentQuestion) 25 else 10)
            }
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun normalizedAnswer(index: Int): Double =
        selectedAnswers[index].toDouble() / LAST_OPTION_INDEX

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private data class Question(
        val title: Int,
        val options: List<String>,
    )

    private companion object {
        const val KEY_QUESTION_INDEX = "question_index"
        const val KEY_SELECTED_ANSWERS = "selected_answers"
        const val ARG_ENVIRONMENT = "preferenceEnvironment"
        const val ARG_SOCIAL = "preferenceSocial"
        const val ARG_PHYSICAL = "preferencePhysical"
        const val ARG_BUDGET = "preferenceBudget"
        const val ARG_CREATIVITY = "preferenceCreativity"
        const val QUESTION_COUNT = 5
        const val NEUTRAL_OPTION_INDEX = 2
        const val LAST_OPTION_INDEX = 4.0
    }
}
