package com.example.exoplayerdemo_v2.presenter

import com.example.exoplayerdemo_v2.MainContract

class MainPresenter(val view : MainContract.View?) : MainContract.Presenter {

    override fun initPresenter() {
        view?.initializePlayer()
    }

    override fun onResume() {
        view?.seekToPosition()
    }

    override fun onStop() {
        view?.releasePlayer()
    }

    override fun requestPIPMode() {
        view?.enterPIPMode()
    }
}