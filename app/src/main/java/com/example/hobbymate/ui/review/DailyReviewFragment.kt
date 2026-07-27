package com.example.hobbymate.ui.review

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.hobbymate.R
import com.example.hobbymate.databinding.FragmentDailyReviewBinding
import com.example.hobbymate.model.DailyReview
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DailyReviewFragment : Fragment() {

    private var _binding: FragmentDailyReviewBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel: ReviewViewModel by viewModels()
    private var selectedPhotoUri: Uri? = null

    private val photoPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri == null || _binding == null) return@registerForActivityResult
        persistReadPermission(uri)
        renderSelectedPhoto(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDailyReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        renderActivitySummary()
        savedInstanceState
            ?.getString(STATE_PHOTO_URI)
            ?.let(Uri::parse)
            ?.let(::renderSelectedPhoto)
        setupPhotoPicker()
        observeSaveState()

        binding.buttonSaveReview.setOnClickListener {
            saveReview()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PHOTO_URI, selectedPhotoUri?.toString())
    }

    private fun renderActivitySummary() {
        val actualMinutes = arguments?.getInt(ARG_ACTUAL_MINUTES, 1)
            ?.coerceAtLeast(1)
            ?: 1
        val completedSteps = arguments?.getInt(ARG_COMPLETED_STEPS, 0) ?: 0
        val totalSteps = arguments?.getInt(ARG_TOTAL_STEPS, 0) ?: 0
        val safeCompletedSteps = completedSteps.coerceIn(0, totalSteps.coerceAtLeast(0))
        val currentStage = arguments?.getString(ARG_CURRENT_STAGE).orEmpty()
        val reviewTitle = arguments?.getString(ARG_REVIEW_TITLE).orEmpty()
            .ifBlank { getString(R.string.daily_review_title) }

        binding.textReviewDate.text = SimpleDateFormat(
            DATE_PATTERN,
            Locale.KOREA,
        ).format(Date())
        binding.editActualMinutes.setText(actualMinutes.toString())
        binding.editReviewTitle.setText(reviewTitle)
        binding.textReviewGoalProgress.text = getString(
            R.string.review_progress_format,
            safeCompletedSteps,
            totalSteps,
        )
        binding.reviewGoalProgress.progress = ReviewFormatter.progressPercent(
            completedSteps = safeCompletedSteps,
            totalSteps = totalSteps,
        )
        binding.textCurrentStage.text = if (currentStage.isBlank()) {
            getString(R.string.current_stage_empty)
        } else {
            getString(
                R.string.current_stage_format,
                safeCompletedSteps.coerceAtLeast(1),
                currentStage,
            )
        }

        val incompleteSteps = (totalSteps - safeCompletedSteps).coerceAtLeast(0)
        binding.carryOverContainer.isVisible = totalSteps > 0 && incompleteSteps > 0
        binding.textCarryOverHint.text = getString(
            R.string.carry_over_dynamic_hint,
            incompleteSteps,
        )
    }

    private fun setupPhotoPicker() {
        binding.photoPickerContainer.setOnClickListener {
            photoPicker.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly,
                ),
            )
        }
        binding.buttonRemovePhoto.setOnClickListener {
            clearSelectedPhoto()
        }
    }

    private fun renderSelectedPhoto(uri: Uri) {
        selectedPhotoUri = uri
        binding.imageReviewPhoto.isVisible = true
        binding.buttonRemovePhoto.isVisible = true
        binding.textAttachPhoto.isVisible = false
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(binding.imageReviewPhoto)
    }

    private fun clearSelectedPhoto() {
        selectedPhotoUri = null
        Glide.with(this).clear(binding.imageReviewPhoto)
        binding.imageReviewPhoto.setImageDrawable(null)
        binding.imageReviewPhoto.isVisible = false
        binding.buttonRemovePhoto.isVisible = false
        binding.textAttachPhoto.isVisible = true
    }

    private fun persistReadPermission(uri: Uri) {
        runCatching {
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
    }

    private fun saveReview() {
        val title = binding.editReviewTitle.text?.toString()?.trim().orEmpty()
        val actualMinutes = binding.editActualMinutes.text
            ?.toString()
            ?.toIntOrNull()

        binding.inputReviewTitle.error = when {
            title.isBlank() -> getString(R.string.review_title_required)
            else -> null
        }
        binding.editActualMinutes.error = when {
            actualMinutes == null || actualMinutes < 1 ->
                getString(R.string.review_minutes_required)
            else -> null
        }
        if (title.isBlank() || actualMinutes == null || actualMinutes < 1) return

        val totalSteps = arguments?.getInt(ARG_TOTAL_STEPS, 0) ?: 0
        val completedSteps = arguments?.getInt(ARG_COMPLETED_STEPS, 0)
            ?.coerceIn(0, totalSteps.coerceAtLeast(0))
            ?: 0
        viewModel.saveDailyReview(
            DailyReview(
                sessionId = arguments?.getLong(ARG_SESSION_ID, 1L) ?: 1L,
                hobbyId = arguments?.getString(ARG_HOBBY_ID)
                    ?: DEFAULT_HOBBY_ID,
                weekNumber = arguments?.getInt(ARG_WEEK_NUMBER, 1) ?: 1,
                title = title,
                actualMinutes = actualMinutes,
                note = binding.editReview.text?.toString()?.trim().orEmpty(),
                hardPart = binding.editHardPart.text?.toString()?.trim().orEmpty(),
                satisfaction = binding.ratingSatisfaction.rating.toInt(),
                completedSteps = completedSteps,
                totalSteps = totalSteps,
                currentStage = arguments?.getString(ARG_CURRENT_STAGE).orEmpty(),
                imageUri = selectedPhotoUri?.toString(),
            ),
        )
    }

    private fun observeSaveState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.buttonSaveReview.isEnabled = !state.isSaving
                    binding.buttonSaveReview.text = getString(
                        if (state.isSaving) R.string.saving else R.string.save_record,
                    )
                    binding.textSaveError.isVisible = state.errorMessage != null
                    binding.textSaveError.text = state.errorMessage
                        ?: getString(R.string.review_save_failed)

                    if (
                        state.isSaved &&
                        findNavController().currentDestination?.id ==
                        R.id.dailyReviewFragment
                    ) {
                        viewModel.onSaveHandled()
                        findNavController().navigate(
                            R.id.action_dailyReviewFragment_to_weeklyReviewFragment,
                            Bundle().apply {
                                putString(
                                    ARG_HOBBY_ID,
                                    arguments?.getString(ARG_HOBBY_ID)
                                        ?: DEFAULT_HOBBY_ID,
                                )
                                putInt(
                                    ARG_WEEK_NUMBER,
                                    arguments?.getInt(ARG_WEEK_NUMBER, 1) ?: 1,
                                )
                                putInt(
                                    ARG_SESSIONS_PER_WEEK,
                                    arguments?.getInt(ARG_SESSIONS_PER_WEEK, 5) ?: 5,
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        if (_binding != null) {
            Glide.with(this).clear(binding.imageReviewPhoto)
        }
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ARG_SESSION_ID = "sessionId"
        const val ARG_REVIEW_TITLE = "reviewTitle"
        const val ARG_ACTUAL_MINUTES = "actualMinutes"
        const val ARG_COMPLETED_STEPS = "completedSteps"
        const val ARG_TOTAL_STEPS = "totalSteps"
        const val ARG_CURRENT_STAGE = "currentStage"
        const val ARG_HOBBY_ID = "hobbyId"
        const val ARG_WEEK_NUMBER = "weekNumber"
        const val ARG_SESSIONS_PER_WEEK = "sessionsPerWeek"
        const val STATE_PHOTO_URI = "selectedPhotoUri"
        const val DATE_PATTERN = "yyyy.MM.dd"
        const val DEFAULT_HOBBY_ID = "climbing"
    }
}
