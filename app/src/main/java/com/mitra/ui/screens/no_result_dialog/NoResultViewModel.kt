package com.mitra.ui.screens.no_result_dialog

import androidx.lifecycle.ViewModel
import com.mitra.network.SocketClient

class NoResultViewModel(
    private val socketClient: SocketClient
) : ViewModel() {

    fun runFinder() {
        socketClient.runFinder()
    }
}