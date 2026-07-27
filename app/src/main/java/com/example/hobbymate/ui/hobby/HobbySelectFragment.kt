package com.example.hobbymate.ui.hobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.data.local.SelectedHobbyStore
import com.example.hobbymate.databinding.FragmentHobbySelectBinding
import com.example.hobbymate.model.HobbyVideoTags
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HobbySelectFragment : Fragment() {

    private var _binding: FragmentHobbySelectBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var selectedHobbyStore: SelectedHobbyStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHobbySelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val groups = listOf(
            binding.chipGroupFitness,
            binding.chipGroupOutdoor,
            binding.chipGroupMusic,
            binding.chipGroupArt,
        )
        val savedSelections = selectedHobbyStore.getAll().toSet()
        groups.forEach { group ->
            group.children.filterIsInstance<Chip>().forEach { chip ->
                chip.isChecked =
                    HobbyVideoTags.idForDisplayName(chip.text.toString()) in savedSelections
                chip.setOnCheckedChangeListener { _, _ -> updateSelectedCount(groups) }
            }
        }
        updateSelectedCount(groups)
        binding.buttonSelectHobby.setOnClickListener {
            val selectedIds = selectedIds(groups)
            selectedHobbyStore.saveAll(selectedIds)
            findNavController().navigate(
                R.id.action_hobbySelectFragment_to_hobbyReadyFragment,
                bundleOf(ARG_HOBBY_IDS to selectedIds.toTypedArray()),
            )
        }
    }

    private fun selectedIds(groups: List<ChipGroup>): List<String> =
        groups.flatMap { group ->
            group.children
                .filterIsInstance<Chip>()
                .filter(Chip::isChecked)
                .map { HobbyVideoTags.idForDisplayName(it.text.toString()) }
                .toList()
        }.distinct()

    private fun updateSelectedCount(groups: List<ChipGroup>) {
        val count = groups.sumOf { group ->
            group.children.filterIsInstance<Chip>().count { it.isChecked }
        }
        binding.textSelectedCount.text = getString(R.string.selected_count, count)
        binding.buttonSelectHobby.isEnabled = count > 0
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ARG_HOBBY_IDS = "hobbyIds"
    }
}
