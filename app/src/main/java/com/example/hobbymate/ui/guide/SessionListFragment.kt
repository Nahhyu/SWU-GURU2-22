package com.example.hobbymate.ui.guide

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.hobbymate.R
import com.example.hobbymate.databinding.FragmentSessionListBinding
import com.example.hobbymate.databinding.ItemYoutubeVideoBinding
import com.example.hobbymate.model.HobbyVideoTags
import com.example.hobbymate.model.YouTubeVideo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SessionListFragment : Fragment() {

    private var _binding: FragmentSessionListBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel by viewModels<GuideViewModel>()
    private var selectedVideoId: String? = null
    private var currentVideos: List<YouTubeVideo> = emptyList()

    private val hobbyId: String
        get() = arguments?.getString(ARG_HOBBY_ID)
            ?: HobbyVideoTags.DEFAULT_HOBBY_ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSessionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedVideoId = savedInstanceState?.getString(KEY_SELECTED_VIDEO_ID)

        val hobby = HobbyVideoTags.get(hobbyId)
        binding.textSessionListTitle.text = getString(
            R.string.session_list_title_format,
            hobby.displayName,
            arguments?.getInt(ARG_WEEK_NUMBER, 1) ?: 1,
        )
        binding.textVideoKeyword.text = getString(
            R.string.video_keyword_format,
            hobby.searchTags.joinToString(" · "),
        )
        binding.buttonRetryVideos.setOnClickListener {
            viewModel.searchVideosForHobby(hobbyId)
        }
        binding.buttonOpenToday.setOnClickListener {
            val selectedVideo = currentVideos.firstOrNull {
                it.videoId == selectedVideoId
            } ?: return@setOnClickListener
            findNavController().navigate(
                R.id.action_sessionListFragment_to_dailyGuideFragment,
                bundleOf(
                    ARG_VIDEO_ID to selectedVideo.videoId,
                    ARG_VIDEO_TITLE to selectedVideo.title,
                    ARG_VIDEO_THUMBNAIL_URL to selectedVideo.thumbnailUrl,
                    ARG_VIDEO_DESCRIPTION to selectedVideo.description,
                    ARG_VIDEO_CHANNEL to selectedVideo.channelName,
                    ARG_HOBBY_ID to hobbyId,
                    ARG_HOBBY_GOAL to arguments?.getString(ARG_HOBBY_GOAL).orEmpty(),
                    ARG_WEEK_NUMBER to (
                        arguments?.getInt(ARG_WEEK_NUMBER, 1) ?: 1
                        ),
                    ARG_SESSIONS_PER_WEEK to (
                        arguments?.getInt(ARG_SESSIONS_PER_WEEK, 5) ?: 5
                        ),
                ),
            )
        }

        observeVideos()
        if (savedInstanceState == null) {
            viewModel.searchVideosForHobby(hobbyId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_SELECTED_VIDEO_ID, selectedVideoId)
        super.onSaveInstanceState(outState)
    }

    private fun observeVideos() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: GuideUiState) {
        currentVideos = state.recommendedVideos
        if (currentVideos.none { it.videoId == selectedVideoId }) {
            selectedVideoId = currentVideos.firstOrNull()?.videoId
        }

        binding.progressVideos.isVisible = state.isLoading
        binding.textVideoStatus.isVisible =
            !state.isLoading && (state.errorMessage != null || currentVideos.isEmpty())
        binding.textVideoStatus.text =
            state.errorMessage ?: getString(R.string.video_search_empty)
        binding.buttonRetryVideos.isVisible = !state.isLoading && state.errorMessage != null
        binding.buttonOpenToday.isEnabled = !state.isLoading && selectedVideoId != null

        renderVideoItems(currentVideos)
    }

    private fun renderVideoItems(videos: List<YouTubeVideo>) {
        binding.videoContainer.removeAllViews()
        videos.forEachIndexed { index, video ->
            val item = ItemYoutubeVideoBinding.inflate(
                layoutInflater,
                binding.videoContainer,
                false,
            )
            item.videoTitle.text = video.title.asDisplayText()
            item.videoChannel.text = video.channelName.asDisplayText()
            item.videoSelected.isChecked = video.videoId == selectedVideoId
            item.root.setOnClickListener {
                selectedVideoId = video.videoId
                renderVideoItems(currentVideos)
                binding.buttonOpenToday.isEnabled = true
            }
            item.videoSelected.setOnClickListener {
                selectedVideoId = video.videoId
                renderVideoItems(currentVideos)
                binding.buttonOpenToday.isEnabled = true
            }
            val layoutParams = item.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = if (index == 0) dp(18) else dp(12)
            item.root.layoutParams = layoutParams
            Glide.with(this)
                .load(video.thumbnailUrl)
                .centerCrop()
                .placeholder(R.drawable.bg_video)
                .error(R.drawable.bg_video)
                .into(item.videoThumbnail)
            binding.videoContainer.addView(item.root)
        }
    }

    private fun String.asDisplayText(): Spanned = HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_LEGACY,
    )

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val ARG_HOBBY_ID = "hobbyId"
        const val ARG_VIDEO_ID = "videoId"
        const val ARG_VIDEO_TITLE = "videoTitle"
        const val ARG_VIDEO_THUMBNAIL_URL = "videoThumbnailUrl"
        const val ARG_VIDEO_DESCRIPTION = "videoDescription"
        const val ARG_VIDEO_CHANNEL = "videoChannel"
        const val ARG_HOBBY_GOAL = "hobbyGoal"
        const val ARG_WEEK_NUMBER = "weekNumber"
        const val ARG_SESSIONS_PER_WEEK = "sessionsPerWeek"
        const val KEY_SELECTED_VIDEO_ID = "selected_video_id"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
