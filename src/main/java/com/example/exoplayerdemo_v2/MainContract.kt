package com.example.exoplayerdemo_v2

interface MainContract {
    interface View {
        fun initView()
        fun initializePlayer()
        fun releasePlayer()
        fun seekToPosition()
        fun enterPIPMode()
    }

    interface Presenter {
        fun initPresenter()
        fun onResume()
        fun onStop()
        fun requestPIPMode()
    }
}