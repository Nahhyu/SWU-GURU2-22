package com.example.hobbymate.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.data.local.SelectedHobbyStore
import com.example.hobbymate.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var selectedHobbyStore: SelectedHobbyStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(SPLASH_DURATION_MS)
            if (findNavController().currentDestination?.id == R.id.splashFragment) {
                findNavController().navigate(
                    if (selectedHobbyStore.hasSelection()) {
                        R.id.action_splashFragment_to_homeFragment
                    } else {
                        R.id.action_splashFragment_to_onboardingFragment
                    },
                )
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val SPLASH_DURATION_MS = 1_200L
    }
}
