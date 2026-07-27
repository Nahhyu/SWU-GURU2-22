package com.example.hobbymate.ui.hobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.data.local.SelectedHobbyStore
import com.example.hobbymate.databinding.FragmentHobbyReadyBinding
import com.example.hobbymate.model.HobbyProfileCatalog
import com.example.hobbymate.ui.common.renderHobbyIcon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HobbyReadyFragment : Fragment() {

    private var _binding: FragmentHobbyReadyBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var selectedHobbyStore: SelectedHobbyStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHobbyReadyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val selectedIds = arguments
            ?.getStringArray(ARG_HOBBY_IDS)
            ?.toList()
            .orEmpty()
            .ifEmpty { selectedHobbyStore.getAll() }
        renderSelectedHobbies(selectedIds)
        binding.buttonCreateGuide.setOnClickListener {
            findNavController().navigate(
                R.id.action_hobbyReadyFragment_to_homeFragment,
            )
        }
    }

    private fun renderSelectedHobbies(hobbyIds: List<String>) {
        binding.selectedHobbyContainer.removeAllViews()
        hobbyIds.forEachIndexed { index, hobbyId ->
            val profile = HobbyProfileCatalog.get(hobbyId)
            binding.selectedHobbyContainer.addView(
                LinearLayout(requireContext()).apply {
                    gravity = android.view.Gravity.CENTER
                    orientation = LinearLayout.HORIZONTAL
                    setBackgroundResource(R.drawable.bg_input)
                    setPadding(dp(7), dp(5), dp(18), dp(5))
                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        dp(54),
                    ).apply {
                        topMargin = if (index == 0) 0 else dp(8)
                    }
                    addView(
                        TextView(requireContext()).apply {
                            gravity = android.view.Gravity.CENTER
                            textSize = 20f
                            renderHobbyIcon(profile)
                        },
                        LinearLayout.LayoutParams(dp(42), dp(42)),
                    )
                    addView(
                        TextView(requireContext()).apply {
                            text = profile.displayName
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.on_background,
                                ),
                            )
                            setTypeface(typeface, android.graphics.Typeface.BOLD)
                        },
                        LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        ).apply {
                            marginStart = dp(12)
                        },
                    )
                },
            )
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ARG_HOBBY_IDS = "hobbyIds"
    }
}
