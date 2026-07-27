package com.example.hobbymate.ui.hobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.data.local.SelectedHobbyStore
import com.example.hobbymate.databinding.FragmentHobbyInfoBinding
import com.example.hobbymate.model.HobbyProfileCatalog
import com.example.hobbymate.ui.common.renderHobbyIcon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HobbyInfoFragment : Fragment() {

    private var _binding: FragmentHobbyInfoBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var selectedHobbyStore: SelectedHobbyStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHobbyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hobbyId = arguments?.getString(ARG_HOBBY_ID) ?: DEFAULT_HOBBY_ID
        val profile = HobbyProfileCatalog.get(hobbyId)
        binding.textHobbyIcon.renderHobbyIcon(profile)
        binding.textHobbyName.text = profile.displayName
        binding.textCostValue.text = profile.estimatedCost
        binding.textPlaceValue.text = profile.requiredPlace
        binding.textDifficultyValue.text = profile.difficulty
        binding.textSuppliesValue.text = profile.supplies

        binding.buttonChooseHobby.setOnClickListener {
            selectedHobbyStore.save(profile.id)
            findNavController().navigate(
                R.id.action_hobbyInfoFragment_to_guideSetupFragment,
                bundleOf(
                    ARG_HOBBY_ID to profile.id,
                ),
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ARG_HOBBY_ID = "hobbyId"
        const val DEFAULT_HOBBY_ID = "climbing"
    }
}
