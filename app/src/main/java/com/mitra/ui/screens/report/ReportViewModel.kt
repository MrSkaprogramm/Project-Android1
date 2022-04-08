package com.mitra.ui.screens.report

import androidx.lifecycle.ViewModel
import com.mitra.network.SocketClient

class ReportViewModel(private val socketClient: SocketClient): ViewModel() {

    fun otherClick() {
        socketClient.isNeedShowReportOther = true
    }

    fun inappropriateClick() {
        socketClient.reportChatCompanion()
        socketClient.isNeedShowSuccessReport = true
    }

    fun spamClick() {
        socketClient.reportChatCompanion()
        socketClient.isNeedShowSuccessReport = true
    }
}
