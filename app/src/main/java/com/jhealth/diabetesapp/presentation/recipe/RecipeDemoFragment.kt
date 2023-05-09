package com.jhealth.diabetesapp.presentation.recipe

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FragmentRecipeDemoBinding


class RecipeDemoFragment : Fragment() {
    private var _binding: FragmentRecipeDemoBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<RecipeDemoFragmentArgs>()

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
    private var mVideoUrl: String? = null
    private var videoUrl: String? = null
    private var mute = false
    private lateinit var mContainer: ViewGroup

    companion object {
        const val PLAYBACK = "playback_position"
        const val ITEM = "current_item"
    }

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            binding.progressBar.isVisible = playbackState == ExoPlayer.STATE_BUFFERING
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeDemoBinding.inflate(inflater, container, false)
        mContainer = container!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoUrl = args.args
        Log.d("LIFE", "ON-CREATE")
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYBACK)
            currentItem = savedInstanceState.getInt(ITEM)
            player?.let {
                it.seekTo(savedInstanceState.getInt(ITEM), savedInstanceState.getLong(PLAYBACK))
                it.play()
            }
        }

        binding.playerView.findViewById<ImageView>(R.id.undo)
            .setOnClickListener {
                findNavController().navigateUp()
            }
        binding.playerView.findViewById<ImageView>(R.id.mute_icon)
            .setOnClickListener {
                mute = !mute
                toggleMute(mute)
            }

        extractYoutubeUrl()
    }

    private fun extractYoutubeUrl() {
        val mExtractor: YouTubeExtractor =
            @SuppressLint("StaticFieldLeak")
            object : YouTubeExtractor(requireContext()) {
                override fun onExtractionComplete(
                    sparseArray: SparseArray<YtFile>?,
                    videoMeta: VideoMeta?
                ) {

                    sparseArray?.let {
                        mVideoUrl = sparseArray[18].url
                        Log.d("dia_youtube", "onExtractionComplete: $sparseArray")
                        initialize(sparseArray[18].url)
                    } ?: Toast.makeText(
                        requireContext(),
                        "Some error occurred while parsing video",
                        Toast.LENGTH_SHORT
                    ).show()


                }
            }
        mExtractor.extract(videoUrl)
    }

    private fun initialize(mVideoUrl: String) {
        try{
            player = ExoPlayer.Builder(requireContext()).build()
            binding.playerView.player = player
            val mediaItem = MediaItem.fromUri(mVideoUrl)
            player?.let {
                it.addListener(listener)
                it.setMediaItem(mediaItem)
                it.playWhenReady = playWhenReady
                it.seekTo(currentItem, playbackPosition)
                it.prepare()
            }
        }catch (e:Exception){
            Toast.makeText(
                requireContext(),
                "Some error occurred while playing video",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun release() {
        player?.let {
            playbackPosition = it.contentPosition
            currentItem = it.currentMediaItemIndex
            playWhenReady = it.playWhenReady
            it.release()
        }
        player = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        player?.let {
            outState.putLong(PLAYBACK, it.contentPosition)
            outState.putInt(ITEM, it.currentMediaItemIndex)
        }

    }


    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            mVideoUrl?.let {
                initialize(it)
            }
        }
        Log.d("LIFE", "ON-START")
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            mVideoUrl?.let {
                initialize(it)
            }
        }
        Log.d("LIFE", "ON-RESUME")
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            release()
        }
        Log.d("LIFE", "ON-PAUSE")

    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            release()
        }
        Log.d("LIFE", "ON-STOP")

    }

 private fun toggleMute(mute: Boolean) {
        val muteIcon = binding.playerView.findViewById<ImageView>(R.id.mute_icon)
        player?.volume = if (mute){
            muteIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.volume_off_icon
            ))
            0F

        } else {
            muteIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.volume_up_icon
            ))
            1F
        }
    }

}