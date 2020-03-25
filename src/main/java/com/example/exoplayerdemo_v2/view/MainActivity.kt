package com.example.exoplayerdemo_v2.view

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.example.exoplayerdemo_v2.MainContract
import com.example.exoplayerdemo_v2.R
import com.example.exoplayerdemo_v2.presenter.MainPresenter
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class MainActivity : AppCompatActivity(), MainContract.View {

    lateinit var presenter: MainContract.Presenter
    lateinit var player: SimpleExoPlayer
    lateinit var playerView: PlayerView
    lateinit var mediaSessionConnector: MediaSessionConnector
    lateinit var mediaSessionCompat: MediaSessionCompat
    var playbackPosition: Long = 0L
    var currentWindow: Int = 0
    var isInPIPMode: Boolean = false
    var isPIPEnabled: Boolean = true
    var playWhenReady: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onStart() {
        super.onStart()
        presenter.initPresenter()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        playbackPosition = player.currentPosition
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun initView() {
        playerView = findViewById(R.id.playerView)
        presenter = MainPresenter(this)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            && isPIPEnabled) {
            presenter.requestPIPMode()
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        presenter.requestPIPMode()
    }

    override fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        val dataSourceFactory = DefaultDataSourceFactory(this, "ExoPlayerDemo")
        val uri:Uri = Uri.parse(getString(R.string.mp4_media_url))
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        player.prepare(mediaSource)
        player.seekTo(currentWindow, playbackPosition)
        player.playWhenReady = playWhenReady

        mediaSessionCompat = MediaSessionCompat(this, packageName)
        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat)
        mediaSessionConnector.setPlayer(player)
        mediaSessionCompat.isActive = true

    }

    override fun seekToPosition() {
        if (playbackPosition > 0L && !isInPIPMode) {
            player.seekTo(playbackPosition)
        }
        playerView.useController = true
    }

    override fun releasePlayer() {
        playWhenReady = player.playWhenReady
        currentWindow = player.currentWindowIndex
        mediaSessionConnector.setPlayer(null)
        mediaSessionCompat.isActive = false
        playerView.player = null
        player.release()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        if (newConfig != null) {
            playbackPosition = player.currentPosition
            isInPIPMode = !isInPictureInPictureMode
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun enterPIPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            playerView.useController = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val params = PictureInPictureParams.Builder()
                enterPictureInPictureMode(params.build())
            }
            else {
                enterPictureInPictureMode()
            }
        }
    }
}
