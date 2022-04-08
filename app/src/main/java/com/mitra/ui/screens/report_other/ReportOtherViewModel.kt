package com.mitra.ui.screens.report_other

import androidx.lifecycle.ViewModel
import com.mitra.network.SocketClient

class ReportOtherViewModel(private val socketClient: SocketClient): ViewModel() {

    fun sendOther(text: String) {
        println(text)
        socketClient.reportChatCompanion()
    }

    fun resetNeedShowReportOther() {
        socketClient.isNeedShowReportOther = false
    }
}