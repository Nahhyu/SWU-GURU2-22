package com.example.hobbymate.ui.guide

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.widget.CompoundButtonCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.hobbymate.R
import com.example.hobbymate.databinding.FragmentDailyGuideBinding
import com.example.hobbymate.databinding.ItemGuideChecklistStepBinding
import com.example.hobbymate.model.HobbyProfileCatalog
import com.example.hobbymate.model.VideoAnalysisRequest
import com.example.hobbymate.model.VideoChecklistStep
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DailyGuideFragment : Fragment() {

    private var _binding: FragmentDailyGuideBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel by viewModels<DailyGuideViewModel>()
    private lateinit var analysisRequest: VideoAnalysisRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDailyGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val videoId = arguments?.getString(ARG_VIDEO_ID).orEmpty()
        val videoTitle = arguments?.getString(ARG_VIDEO_TITLE).orEmpty()
        val thumbnailUrl = arguments?.getString(ARG_VIDEO_THUMBNAIL_URL).orEmpty()
        val hobbyId = arguments?.getString(ARG_HOBBY_ID).orEmpty()
            .ifBlank { HobbyProfileCatalog.DEFAULT_HOBBY_ID }
        val hobby = HobbyProfileCatalog.get(hobbyId)
        val displayVideoTitle = videoTitle.asPlainText()

        binding.textSelectedVideoTitle.isVisible = videoTitle.isNotBlank()
        binding.textSelectedVideoTitle.text = displayVideoTitle
        binding.textDailyActivityTitle.text = getString(
            R.string.daily_activity_title_format,
            hobby.displayName,
        )
        analysisRequest = VideoAnalysisRequest(
            videoId = videoId,
            title = displayVideoTitle,
            description = arguments?.getString(ARG_VIDEO_DESCRIPTION).orEmpty(),
            thumbnailUrl = thumbnailUrl,
            channelName = arguments?.getString(ARG_VIDEO_CHANNEL).orEmpty(),
            hobbyName = hobby.displayName,
            goal = arguments?.getString(ARG_HOBBY_GOAL).orEmpty()
                .ifBlank { hobby.sampleGoal },
        )
        setupYouTubePlayer(videoId)
        observeAnalysis()
        binding.buttonRetryAnalysis.setOnClickListener {
            viewModel.analyze(analysisRequest, force = true)
        }
        binding.buttonCompleteSession.setOnClickListener {
            val state = viewModel.uiState.value
            val checklist = state.checklist ?: return@setOnClickListener
            val currentStage = checklist.steps
                .getOrNull((state.completedStepCount - 1).coerceAtLeast(0))
                ?.title
                .orEmpty()
            findNavController().navigate(
                R.id.action_dailyGuideFragment_to_dailyReviewFragment,
                Bundle().apply {
                    putLong(ARG_SESSION_ID, 1L)
                    putString(ARG_REVIEW_TITLE, checklist.title)
                    putInt(ARG_ACTUAL_MINUTES, viewModel.actualMinutes())
                    putInt(ARG_COMPLETED_STEPS, state.completedStepCount)
                    putInt(ARG_TOTAL_STEPS, checklist.steps.size)
                    putString(ARG_CURRENT_STAGE, currentStage)
                    putString(
                        ARG_HOBBY_ID,
                        arguments?.getString(ARG_HOBBY_ID)
                            ?: HobbyProfileCatalog.DEFAULT_HOBBY_ID,
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
        viewModel.analyze(analysisRequest)
    }

    private fun observeAnalysis() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::renderAnalysis)
            }
        }
    }

    private fun renderAnalysis(state: DailyGuideUiState) {
        val checklist = state.checklist
        binding.analysisLoadingContainer.isVisible = state.isAnalyzing
        binding.analysisErrorContainer.isVisible = state.errorMessage != null
        binding.textAnalysisError.text = state.errorMessage.orEmpty()
        binding.checklistContainer.isVisible = checklist != null
        binding.textGuideProgress.isVisible = checklist != null
        binding.guideProgress.isVisible = checklist != null
        binding.buttonCompleteSession.isEnabled = state.isComplete

        if (checklist == null) {
            binding.checklistContainer.removeAllViews()
            return
        }

        binding.textDailyActivityTitle.text = checklist.title
        binding.textEstimatedDuration.text = getString(
            R.string.estimated_duration_format,
            checklist.estimatedMinutes,
        )
        binding.textGuideProgress.text = getString(
            R.string.guide_progress_format,
            state.completedStepCount,
            checklist.steps.size,
        )
        binding.guideProgress.progress =
            state.completedStepCount * 100 / checklist.steps.size
        renderChecklist(
            steps = checklist.steps,
            completedStepCount = state.completedStepCount,
        )
    }

    private fun renderChecklist(
        steps: List<VideoChecklistStep>,
        completedStepCount: Int,
    ) {
        binding.checklistContainer.removeAllViews()
        steps.forEachIndexed { index, step ->
            val item = ItemGuideChecklistStepBinding.inflate(
                layoutInflater,
                binding.checklistContainer,
                false,
            )
            val isCompleted = index < completedStepCount
            val isActive = index == completedStepCount
            val tintColor = when {
                isCompleted -> R.color.success
                isActive -> R.color.primary
                else -> R.color.outline_strong
            }

            item.stepTitle.text = getString(
                R.string.step_number_format,
                index + 1,
                step.title,
            )
            item.stepDescription.text = step.description
            item.stepDescription.isVisible = step.description.isNotBlank()
            item.stepMinutes.text = getString(
                R.string.step_minutes_format,
                step.estimatedMinutes,
            )
            item.stepCheck.isChecked = isCompleted
            item.stepCheck.isEnabled = isCompleted || isActive
            CompoundButtonCompat.setButtonTintList(
                item.stepCheck,
                ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), tintColor),
                ),
            )
            item.stepContainer.setBackgroundResource(
                if (isActive) R.drawable.bg_card_selected else R.drawable.bg_card,
            )
            item.stepContainer.alpha = if (isCompleted || isActive) 1f else 0.52f
            item.stepContainer.isClickable = isActive
            item.stepContainer.setOnClickListener(
                if (isActive) {
                    View.OnClickListener { viewModel.completeStep(index) }
                } else {
                    null
                },
            )
            val layoutParams = item.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = if (index == 0) dp(12) else dp(9)
            item.root.layoutParams = layoutParams
            binding.checklistContainer.addView(item.root)
        }
    }

    private fun String.asPlainText(): String = HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_LEGACY,
    ).toString()

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupYouTubePlayer(videoId: String) {
        val safeVideoId = videoId.takeIf(VIDEO_ID_PATTERN::matches)
        binding.youtubePlayer.isVisible = safeVideoId != null
        binding.textVideoUnavailable.isVisible = safeVideoId == null
        binding.progressVideoPlayer.isVisible = safeVideoId != null
        if (safeVideoId == null) return

        binding.youtubePlayer.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = true
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?,
                ) {
                    binding.progressVideoPlayer.isVisible = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressVideoPlayer.isVisible = false
                }
            }
            loadDataWithBaseURL(
                "$PLAYER_ORIGIN/",
                playerHtml(safeVideoId),
                "text/html",
                Charsets.UTF_8.name(),
                null,
            )
        }
    }

    private fun playerHtml(videoId: String): String = """
        <!doctype html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                html, body, #player {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    padding: 0;
                    overflow: hidden;
                    background: #000;
                }
            </style>
        </head>
        <body>
            <div id="player"></div>
            <script src="https://www.youtube.com/iframe_api"></script>
            <script>
                var player;
                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        width: '100%',
                        height: '100%',
                        videoId: '$videoId',
                        playerVars: {
                            playsinline: 1,
                            rel: 0,
                            origin: '$PLAYER_ORIGIN'
                        }
                    });
                }
                function pauseVideo() {
                    if (player && player.pauseVideo) player.pauseVideo();
                }
            </script>
        </body>
        </html>
    """.trimIndent()

    override fun onPause() {
        if (_binding != null) {
            binding.youtubePlayer.evaluateJavascript("pauseVideo()", null)
            binding.youtubePlayer.onPause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) binding.youtubePlayer.onResume()
    }

    override fun onDestroyView() {
        binding.youtubePlayer.apply {
            loadUrl("about:blank")
            stopLoading()
            webChromeClient = null
            webViewClient = WebViewClient()
            removeAllViews()
            destroy()
        }
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ARG_VIDEO_ID = "videoId"
        const val ARG_VIDEO_TITLE = "videoTitle"
        const val ARG_VIDEO_THUMBNAIL_URL = "videoThumbnailUrl"
        const val ARG_VIDEO_DESCRIPTION = "videoDescription"
        const val ARG_VIDEO_CHANNEL = "videoChannel"
        const val ARG_HOBBY_ID = "hobbyId"
        const val ARG_HOBBY_GOAL = "hobbyGoal"
        const val ARG_SESSION_ID = "sessionId"
        const val ARG_REVIEW_TITLE = "reviewTitle"
        const val ARG_ACTUAL_MINUTES = "actualMinutes"
        const val ARG_COMPLETED_STEPS = "completedSteps"
        const val ARG_TOTAL_STEPS = "totalSteps"
        const val ARG_CURRENT_STAGE = "currentStage"
        const val ARG_WEEK_NUMBER = "weekNumber"
        const val ARG_SESSIONS_PER_WEEK = "sessionsPerWeek"
        const val PLAYER_ORIGIN = "https://hobbymate.app"
        val VIDEO_ID_PATTERN = Regex("^[A-Za-z0-9_-]{11}$")
    }
}
